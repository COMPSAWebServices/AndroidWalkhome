package gm.googlemapsproject.com.googlemapsproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;//for google places
    private Circle circle;
    private double currentLat = 0;
    private double currentLong = 0;
    private LocationManager locationManager; //to get the user's current location from gps
    private String provider;
    private boolean enabled;
    private static final String TAG = "";

    private double latFrom;
    private double longFrom;
    private double latTo;
    private double longTo;
    public boolean flag;

    private LocationRequest currentLocationRequest;
    private Marker currentLocationMarker;


    private int duration = Toast.LENGTH_SHORT;
    ;//toast length

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));

        getSupportActionBar().setTitle("WalkHome");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        ;//toast length


        //googlemaps
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_map);
        mapFragment.getMapAsync(this);


        try {
            //gets the intent from DirectionActivity
            Intent directionActivityIntent = this.getIntent();
            Bundle bundle = directionActivityIntent.getExtras();
            //String intentMessage = intent.getStringExtra("currentLocation");
            latFrom = bundle.getDouble("latFrom");
            longFrom = bundle.getDouble("longFrom");
            latTo = bundle.getDouble("latTo");
            longTo = bundle.getDouble("longTo");
            flag = bundle.getBoolean("directionSent");

            currentLat = latFrom;
            currentLong = longFrom;

        } catch (Exception e) {
            //if the application just started and can't the bundle from DirectionActivity.
//            if ((currentLat == 0) && (currentLong == 0)) {
//                Toast toast = Toast.makeText(context, "setting current location", duration);
//                //toast.show();
//               // setCurrentLocation();
//            }
        }


        Toast toast = Toast.makeText(context, Double.toString(latFrom), duration);
        //toast.show();

        //floating button -> ***Change this later
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //calls setDirection if we know that the user has already entered their destination
        if (flag == true) {
            setDirections();
        }


    }//end onCreate

    /**Get the directions**/
    public void setDirections() {
        //requires server key instead of the api key
        GoogleDirection.withServerKey("AIzaSyA5w5A7o_V_gyeNwsTg_pjjN1e7eF_la4s")
                .from(new LatLng(currentLat, currentLong))
                .to(new LatLng(latTo, longTo))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                            /*
                            @Override
                            public void onDirectionSuccess(Path.Direction direction, String rawBody) {
                                if(direction.isOK()) {
                                    // Do something
                                } else {
                                    // Do something
                                }
                            }*/

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        String status = direction.getStatus();
                        //add the other market
                        LatLng test = new LatLng(latTo, longTo);
                        mMap.addMarker(new MarkerOptions().position(test).title("Destination"));
                        if (status.equals(RequestResult.OK)) {
                            // Do something
                            //Direction from origin to destination location
                            Route route = direction.getRouteList().get(0);
                            //Leg: the direction way from one location to another location
                            Leg leg = route.getLegList().get(0);

                            //To draw a direction route on google maps, it must be a PolylineOptions
                            //So we have to convert the route into Poly. by retrieving leg instance
                            //from route instance, then converting it by using DirectionCoverter class.
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(NavigationActivity.this, directionPositionList, 5, Color.RED);
                            mMap.addPolyline(polylineOptions);

                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            // Do something
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }

                });
    }


    /***********************************************CURRENT LOCATION**************************************************************/
    /*Current location from GPS_PROVIDER gets the last known location, might not be accurate so we're going to have to change this.*/

//    public void setCurrentLocation() {
//        //gets location manager
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        provider = locationManager.getBestProvider(criteria, false);
//        Context context = getApplicationContext();
//        try {
//            Location location = locationManager.getLastKnownLocation(provider);
//            if (location != null) {
//                getCurrentLocation(location);
//            } else {//if location not found
//                //set location to queens campus
//                setDefaultLocation();
//                enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//                //checks if gps is not enabled
//                if (!enabled) {
//                    /*new AlertDialog.Builder(MapsActivity.this); ==> so its not applicationContext, it needs to be <currentactivity>.this*/
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//                    builder1.setMessage("Please turn on your GPS!");
//                    builder1.setCancelable(true);
//
//                    builder1.setPositiveButton(
//                            "Ok",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                    /* take them to enable their gps
//                                    * Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                    startActivity(intent);*/
//                                }
//                            });
//
//                    /*Maybe take this out*/
//                    builder1.setNegativeButton(
//                            "No",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert11 = builder1.create();
//                    alert11.show();
//                } else {
//                    //gps is on
//                }
//            }
//        } catch (SecurityException e) {
//
//            //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
//            Toast toast = Toast.makeText(context, "Please turn on your GPS!", duration);
//            toast.show();
//        }//end catch
//    }//end setCurrentLocation
//
//    /*gets the current location from the user and stores it into currentLat and currentLong*/
//    public void getCurrentLocation(Location location) {
//
//        currentLat = location.getLatitude();
//        currentLong = location.getLongitude();
//    }//end getCurrentLocation;
//
//    //if gps is not set, sets the location somewhere
//    public void setDefaultLocation() {
//        Context context = getApplicationContext();
//        int duration = Toast.LENGTH_SHORT;
//        ;//toast length
//        Toast toast = Toast.makeText(context, "Setting default Location!", duration);
//        toast.show();
//        //LatLng queens = new LatLng(44.053607, -79.458481);
//        currentLat = 44.053607;
//        currentLong = -79.458481;
//    }//end setDefaultLocation

    /***********************************************CURRENT LOCATION**************************************************************/

    /***********************************************GGOOGLE MAPS**************************************************************/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*
        // Location currently set in Newmarket, ON
        //LatLng queens = new LatLng(44.053607, -79.458481);
        LatLng queens = new LatLng(currentLat, currentLong);
        //adds the marker description
        mMap.setMyLocationEnabled(true);
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




        //adds the specify location and zoom in by 17
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(queens, 15));

        //adds a circle radius around the specifiy location
        circle = mMap.addCircle(new CircleOptions()
                .center(queens)
                .radius(1000)
                .strokeWidth(10)
                .strokeColor(Color.GREEN)
                .fillColor(0x5500ff00)
                .clickable(true));

            */

        //TESTING
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }//end on mapready
    /***********************************************GGOOGLE MAPS**************************************************************/




    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    //sets the time interval
    @Override
    public void onConnected(Bundle bundle) {
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
        Location userLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (userLastLocation != null) {
            currentLat = userLastLocation.getLatitude();
            currentLong = userLastLocation.getLongitude();
            LatLng currentLatLong = new LatLng(currentLat, currentLong);
            MarkerOptions currentMarker = new MarkerOptions();
            currentMarker.position(currentLatLong);
            currentMarker.title("Current Location");
            currentLocationMarker = mMap.addMarker(currentMarker);
            //sets the map at the current location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 16));

        }
        //checks for current location every interval
        currentLocationRequest = new LocationRequest();
        currentLocationRequest.setInterval(50000); //50 seconds
        currentLocationRequest.setFastestInterval(60000); //60 seconds
        currentLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //google maps current location
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, currentLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
        LatLng newCurrentLatLng = new LatLng(currentLat,currentLong );
        MarkerOptions currentMarker = new MarkerOptions();
        currentMarker.position(newCurrentLatLng);
        currentMarker.title("Current Location");
        currentLocationMarker = mMap.addMarker(currentMarker);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(newCurrentLatLng).zoom(16).build();

        //mMap.animateCamera(CameraUpdateFactory
                //.newCameraPosition(cameraPosition));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCurrentLatLng, 16));


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
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /***********************************************NAVIGATION DRAWER**************************************************************/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about_walkhome) {
            // Handle the camera action
            Intent startActivityInformation = new Intent(NavigationActivity.this, InformationActivity.class);
            startActivity(startActivityInformation);
        } else if (id == R.id.about_campus_security) {

        } else if (id == R.id.request_walk) {
            //setCurrentLocation();
            Intent currentLocationIntent = new Intent(NavigationActivity.this, DirectionActivity.class);
            Bundle setBundle = new Bundle();
            setBundle.putDouble("currentLat", currentLat);
            setBundle.putDouble("currentLong", currentLong);
            currentLocationIntent.putExtras(setBundle);
            startActivity(currentLocationIntent);
        }
         /*
        else if (id == R.id.nav_manage) {

        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
