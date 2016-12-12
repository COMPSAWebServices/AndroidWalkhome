package com.compsawebservices.walkhome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

import com.compsawebservices.walkhome.R;

/**
 * Sets up two autocomplete fragments for TO and FROM
 * */
public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;//for google places
    private double currentLat = 0;
    private double currentLong = 0;
    private String currentAddress = "";

    private LatLng latlngFrom;
    private LatLng latlngTo;
    private double latTo;
    private double longTo;

    private static final String TAG = "";
    private Button requestButton;
    private Button walkhomeInfo;
    private Button callWalkhome;
    private String currentLocation;
    private String destination;

    private int duration = Toast.LENGTH_SHORT;;//toast length
    private Boolean flag = false;
    private String testing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //displays the back  button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        walkhomeInfo = (Button)findViewById(R.id.direction_act_info);
        callWalkhome = (Button)findViewById(R.id.direction_act_call);

        walkhomeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DirectionActivity.this, InformationActivity.class);
                startActivity(i);
            }
        });

        //calls walkhome
        callWalkhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callWalkHome = new Intent(Intent.ACTION_DIAL);
                callWalkHome.setData(Uri.parse("tel:6135339255"));
                if (ActivityCompat.checkSelfPermission(DirectionActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callWalkHome);
            }
        });//end walkhome setonclicklistener

        Intent navigationActivityIntent = this.getIntent();
        Bundle bundleCurrentLocation = navigationActivityIntent.getExtras();
        currentLat     = bundleCurrentLocation.getDouble("currentLat");
        currentLong    = bundleCurrentLocation.getDouble("currentLong");
        currentAddress = bundleCurrentLocation.getString("current_address");

        //initialize the googleapi client for autocomplete
        //https://developers.google.com/places/android-api/start
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //autocomplete  from
        PlaceAutocompleteFragment autocompleteFragmentFrom = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_from);
        autocompleteFragmentFrom.setHint(currentAddress);

        //To bias autocomplete results to a specific geographic region
        autocompleteFragmentFrom.setBoundsBias(new LatLngBounds(new LatLng(currentLat, currentLong), new LatLng(currentLat, currentLong) ));
        autocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public void onPlaceSelected(Place place) {
                currentLocation = place.getName().toString();
                latlngFrom = place.getLatLng();
                currentLat = latlngFrom.latitude;
                currentLong = latlngFrom.longitude;
            }
            @Override
            public void onError(Status status) {
            }
        });


        //autocomplete to
        PlaceAutocompleteFragment autocompleteFragmentTo = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_to);
        autocompleteFragmentTo.setHint("Destination");
        //To bias autocomplete results to a specific geographic region
        autocompleteFragmentTo.setBoundsBias(new LatLngBounds(new LatLng(currentLat, currentLong), new LatLng(currentLat, currentLong) ));
        autocompleteFragmentTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination = place.getName().toString();
                latlngTo = place.getLatLng();
                latTo = latlngTo.latitude;
                longTo = latlngTo.longitude;
            }

            @Override
            public void onError(Status status) {
            }
        });

        //request button redirects the page back to NavigationActivity
        requestButton = (Button)findViewById(R.id.request_button);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectionActivity.this, NavigationActivity.class);
                Context context = getApplicationContext();
                Toast toast;
                flag = true;
                //checks that destination has been filled
                if(latTo == 0 && longTo == 0){
                    toast = Toast.makeText(context, "Please enter a destination!", duration);
                    toast.show();
                    flag = false;
                }
                //transfer Intent to NavigationAcitivty if it satisfies all the requirements
                if(flag==true){
                    final Geocoder geocoder = new Geocoder(DirectionActivity.this, Locale.getDefault());
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latFrom", currentLat);
                    bundle.putDouble("longFrom", currentLong);
                    bundle.putDouble("latTo", latTo);
                    bundle.putDouble("longTo", longTo);
                    bundle.putString("page", "directionAct");
                    try {
                        List<Address> addressesFrom = geocoder.getFromLocation(currentLat, currentLong, 1);
                        String addressFrom = addressesFrom.get(0).getAddressLine(0);

                        List<Address> addressesTo = geocoder.getFromLocation(latTo,longTo , 1);
                        String addressTo = addressesTo.get(0).getAddressLine(0);

                        bundle.putString("current_address_from", addressFrom);
                        bundle.putString("current_address_to", addressTo);

                    } catch(Exception e) {

                    }

                    bundle.putBoolean("directionSent", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });//end requestButton setOnClick...
    }//end onCreate


    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id==android.R.id.home){
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}