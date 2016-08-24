package gm.googlemapsproject.com.googlemapsproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/*Google maps places api import*/
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnConnectionFailedListener {

    private static final String TAG = "";
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Circle circle;
    private GoogleApiClient mGoogleApiClient;//for google places
    private LocationManager locationManager; //to get the user's current location from gps
    private String provider;

    private double currentLat;
    private double currentLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;;//toast length
        boolean enabled; //to check if gps is enabled or not

        //initialize the googleapi client for autocomplete
        //**Actually not sure what this does. GOing to have to read more about it
        //https://developers.google.com/places/android-api/start
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        //auto complete fragment
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Search a Location");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }

        });

        //get location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        try{





            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null){
                getCurrentLocation(location);
            }else{//if location not found
                /*
                setDefaultLocation();
                Toast toast = Toast.makeText(context, "GPS not enabled!", duration);
                */


                enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                //checks if gps is not enabled
                if (!enabled){
                    /*new AlertDialog.Builder(MapsActivity.this); ==> so its not applicationContext, it needs to be <currentactivity>.this*/
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
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



    }//end oncreate

    /*gets the current location from the user and stores it into currentLat and currentLong*/
    public void getCurrentLocation(Location location){
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
    }//end getCurrentLocation();

    //if gps is not set, sets the location somewhere
    public void setDefaultLocation(){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;;//toast length
        Toast toast = Toast.makeText(context, "GPS not enabled!", duration);
        //LatLng queens = new LatLng(44.053607, -79.458481);
        currentLat = 44.053607;
        currentLong = -79.458481;

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Location currently set in Newmarket, ON
        //LatLng queens = new LatLng(44.053607, -79.458481);
        LatLng queens = new LatLng(currentLat, currentLong);
        //adds the market description
        mMap.addMarker(new MarkerOptions().position(queens).title("Marker at Queen's"));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //when clicked move camera to user's current location
        mMap.setMyLocationEnabled(true);

        //adds the specify location and zoom in by 17
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(queens, 17));

        //adds a circle radius around the specifiy location
        circle = mMap.addCircle(new CircleOptions()
                .center(queens)
                .radius(1000)
                .strokeWidth(10)
                .strokeColor(Color.GREEN)
                .fillColor(0x5500ff00)
                .clickable(true));

    }//end on mapready

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO: implement GoogleApiClient.OnConnectionFailedListener
        //to handle connection failures
    }


}
