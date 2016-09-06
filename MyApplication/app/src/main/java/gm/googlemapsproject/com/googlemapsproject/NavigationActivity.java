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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Circle circle;
    private double currentLat;
    private double currentLong;
    private LocationManager locationManager; //to get the user's current location from gps
    private String provider;
    private boolean enabled;


    private int duration = Toast.LENGTH_SHORT;;//toast length

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        //Context context = getApplicationContext();
        //int duration = Toast.LENGTH_SHORT;;//toast length

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

        //googlemaps

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.content_map);
        mapFragment.getMapAsync(this);

        //gets the current location of the user
        setCurrentLocation();

    }



    /***********************************************CURRENT LOCATION**************************************************************/
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

    /***********************************************CURRENT LOCATION**************************************************************/

    /***********************************************GGOOGLE MAPS**************************************************************/
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(queens, 15));

        //adds a circle radius around the specifiy location
        circle = mMap.addCircle(new CircleOptions()
                .center(queens)
                .radius(1000)
                .strokeWidth(10)
                .strokeColor(Color.GREEN)
                .fillColor(0x5500ff00)
                .clickable(true));

    }//end on mapready
    /***********************************************GGOOGLE MAPS**************************************************************/

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
