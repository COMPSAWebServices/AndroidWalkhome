package gm.googlemapsproject.com.googlemapsproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.location.Geocoder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private String currentLocation;
    private String destination;

    private int duration = Toast.LENGTH_SHORT;;//toast length
    private Boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //displays the back  button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        Intent navigationActivityIntent = this.getIntent();
        Bundle bundleCurrentLocation = navigationActivityIntent.getExtras();

        currentLat     = bundleCurrentLocation.getDouble("currentLat");
        currentLong    = bundleCurrentLocation.getDouble("currentLong");
        currentAddress = bundleCurrentLocation.getString("current_address");

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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

        //change the background color
        //autocompleteFragmentFrom.getView().setBackgroundColor(Color.BLUE);


        autocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public void onPlaceSelected(Place place) {
                currentLocation = place.getName().toString();
                latlngFrom = place.getLatLng();
//                latFrom = latlngFrom.latitude;
//                longFrom = latlngFrom.longitude;
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

        //request button
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
                    //String message = "Testing";

                    Bundle bundle = new Bundle();
                    bundle.putDouble("latFrom", currentLat);
                    bundle.putDouble("longFrom", currentLong);
                    bundle.putDouble("latTo", latTo);
                    bundle.putDouble("longTo", longTo);
                    bundle.putBoolean("directionSent", true);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }
        });
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
