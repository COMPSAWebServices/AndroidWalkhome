package gm.googlemapsproject.com.googlemapsproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;//for google places
    private double currentLat;
    private double currentLong;

    private LatLng latlngFrom;
    private LatLng latlngTo;
    private double latFrom;
    private double longFrom;
    private double latTo;
    private double longTo;

    private static final String TAG = "";
    private Button requestButton;
    private String currentLocation;
    private String destination;

    private int duration = Toast.LENGTH_SHORT;;//toast length
    private LocationManager locationManager; //to get the user's current location from gps
    private String provider;
    private Boolean enabled;
    private Boolean flag = false;

   // private static DecimalFormat df2 = new DecimalFormat("##.###");

    private TextView testing;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //displays the
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setCurrentLocation();
        //sets latFrom and longFrom to default currentLat and currentLong
//        latFrom = currentLat;
//        longFrom = currentLong;

        //initialize the googleapi client for autocomplete
        //**Actually not sure what this does. GOing to have to read more about it
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

        autocompleteFragmentFrom.setHint("Current Location");

        //To bias autocomplete results to a specific geographic region
        autocompleteFragmentFrom.setBoundsBias(new LatLngBounds(new LatLng(currentLat, currentLong), new LatLng(currentLat, currentLong) ));

        //change the background color
        autocompleteFragmentFrom.getView().setBackgroundColor(Color.BLUE);


        autocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public void onPlaceSelected(Place place) {
                currentLocation = place.getName().toString();
                latlngFrom = place.getLatLng();
//                latFrom = latlngFrom.latitude;
//                longFrom = latlngFrom.longitude;
                currentLat = latlngFrom.latitude;
                currentLong = latlngFrom.longitude;

                testing = (TextView)findViewById(R.id.testingLatLng);
                testing.setText("Lat: " + latFrom + "                    Long: " +longFrom + "  currentLat: "+ currentLat + "  currentLong: " + currentLong );
                //ToDo
                //need to check if the user changes the current location

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

                //checks to see if currentlocation and currentfrom are the same
//                if(((double)(Math.round(latFrom * 1000)/1000))!=((double)(Math.round(currentLat * 1000)/1000))){
//                    currentLat = latFrom;
//                    currentLong = longFrom;
//                }


                //else alerts/reminds them


                //checks that the currentLocation with GPS is the same as from the geocode
                //***Need to reduce the decimal places to just XX.XXX
                //if they are the same just use the current location
                //if latFrom and longFrom are empty, send current location

                //transfer Intent to NavigationAcitivty if it satisfies all the requirements
                if(flag==true){
                    //String message = "Testing";
                    //intent.putExtra("currentLocation", currentLocation);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latFrom", currentLat);
                    bundle.putDouble("longFrom", currentLong);
                    bundle.putDouble("latTo", latTo);
                    bundle.putDouble("longTo", longTo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
    }//end onCreate

    public void setCurrentLocation(){
        //gets location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Context context = getApplicationContext();
        try{
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null){
                getCurrentLocation(location);
            }else{//if location not found
                //set location to queens campus
                setDefaultLocation();
                /*
                setDefaultLocation();
                Toast toast = Toast.makeText(context, "GPS not enabled!", duration);
                */

                enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                //checks if gps is not enabled
                if (!enabled){
                    /*new AlertDialog.Builder(MapsActivity.this); ==> so its not applicationContext, it needs to be <currentactivity>.this*/
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Please turn on your GPS!");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    /* take them to enable their gps
                                    * Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);*/
                                }
                            });

                    /*Maybe take this out*/
                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else{
                    //gps is on
                }
            }
        } catch (SecurityException e){

            //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
            Toast toast = Toast.makeText(context, "Please turn on your GPS!", duration);
            toast.show();
        }//end catch
    }//end setCurrentLocation

    /*gets the current location from the user and stores it into currentLat and currentLong*/
    public void getCurrentLocation(Location location){
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
    }//end getCurrentLocation;

    //if gps is not set, sets the location somewhere
    public void setDefaultLocation() {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        ;//toast length
        Toast toast = Toast.makeText(context, "GPS not enabled!", duration);
        //LatLng queens = new LatLng(44.053607, -79.458481);
        currentLat = 44.053607;
        currentLong = -79.458481;
    }//end setDefaultLocation

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
