package com.compsawebservices.Walkhome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.compsawebservices.Walkhome.R;


public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;//for google places
    private double currentLat = 0;
    private double currentLong = 0;
    private double latFrom;
    private double longFrom;
    private double latTo;
    private double longTo;
    private String currentAddressFrom;
    private String currentAddressTo;

    private String phoneNumberTemp;
    private String phoneNumber;
    private boolean flag = false;
    private LocationRequest currentLocationRequest;
    private Marker currentLocationMarker;

    private Boolean keepPhone = true;

    private Polyline queensBoundary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //sets toolbar colour
        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));
        getSupportActionBar().setTitle("WalkHome");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        //googlemaps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_map);
        mapFragment.getMapAsync(this);

        //gets the information on DirectionAct in a try catch
        //gets the intent from DirectionActivity
        /*
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        phoneNumberTemp = bundle.getString("phonenumber");
        if(phoneNumberTemp!=null || phoneNumberTemp==""){
            phoneNumber = phoneNumberTemp;
        }
        try{

            checkDirectionIntent();
        }catch (Exception e){}
        */




        //this is the super lazy way to do this but its 3:50am...


        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setVisibility(View.GONE);

        //calls setDirection if we know that the user has already entered their destination
        //shows the direction


        if (flag) {

            setDirections();
            assert fab != null;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            //.setAction("Action", null).show();
                    //addWalkToApi();
                    sendDataToWalkhome();

                }
            });
        }
        //change the fab icon to direction when flag if false
        else{
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.direction));
            fab.setOnClickListener(new View.OnClickListener() {
                //redirects to DirectionAct if user clicks on the direction fab icon
                @Override
                public void onClick(View v) {
                    flag = false;

                    final Geocoder geocoder = new Geocoder(NavigationActivity.this, Locale.getDefault());
                    Intent currentLocationIntent = new Intent(NavigationActivity.this, DirectionActivity.class);
                    Bundle setBundle = new Bundle();
                    setBundle.putDouble("currentLat", currentLat);
                    setBundle.putDouble("currentLong", currentLong);
                    try {
                        List<Address> addresses = geocoder.getFromLocation(currentLat, currentLong, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        setBundle.putString("current_address", address);
                    } catch(Exception e){

                    }
                    currentLocationIntent.putExtras(setBundle);
                    startActivity(currentLocationIntent);
                }
            });//end fab onClick
        }//end else
    }//end onCreate

    public void getPhoneNumber(String value){
        phoneNumber = value;
    }

    //Function to handle intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111){
            if(resultCode == RESULT_OK){
                //Bundle bundle = data.getExtras();
                //phoneNumber = data.getStringExtra()
            }
        }
    }
    protected void checkDirectionIntent(){
        try {

            //gets the intent from DirectionActivity
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();

            //String intentMessage = intent.getStringExtra("currentLocation");
            latFrom = bundle.getDouble("latFrom");
            longFrom = bundle.getDouble("longFrom");
            latTo = bundle.getDouble("latTo");
            longTo = bundle.getDouble("longTo");
            currentAddressFrom = bundle.getString("current_address_from");
            currentAddressTo = bundle.getString("current_address_to");
            flag = bundle.getBoolean("directionSent");
            System.out.println(phoneNumber);


            currentLat = latFrom;
            currentLong = longFrom;

        } catch (Exception e) {} //end try-catch
    }



    /**Shows directions**/
    public void setDirections() {
        //requires server key instead of the api key
        GoogleDirection.withServerKey("AIzaSyCYctX1XOL-9KrVqbLUrDSrgABssH5lmTU")
                .from(new LatLng(currentLat, currentLong))
                .to(new LatLng(latTo, longTo))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {

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
    }//ends setDirections


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

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }//end on mapready


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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 14));

        }
        //checks for current location every interval
        currentLocationRequest = new LocationRequest();
        currentLocationRequest.setInterval(50000); //50 seconds
        currentLocationRequest.setFastestInterval(60000); //60 seconds
        currentLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //google maps current location
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, currentLocationRequest, this);

        //poly line

        PolylineOptions queensBoundaryPolyLine = new PolylineOptions()
                //I blame IOS people for making me to this...
                .add(new LatLng(44.219465,-76.507441 ))
                .add(new LatLng(44.221433,-76.507688))
                .add(new LatLng(44.221041,-76.512731))
                .add(new LatLng(44.223515,-76.512965))
                .add(new LatLng(44.222900,-76.515014))
                .add(new LatLng(44.223500,-76.515229))
                .add(new LatLng(44.223592,-76.516162))
                .add(new LatLng(44.224353,-76.516237))
                .add(new LatLng(44.224330,-76.516977))
                .add(new LatLng(44.229039,-76.517526))
                .add(new LatLng(44.232393,-76.518086))
                .add(new LatLng(44.231913,-76.520351))
                .add(new LatLng(44.232005,-76.520914))
                .add(new LatLng(44.232612,-76.522405))
                .add(new LatLng(44.233012,-76.522845))
                .add(new LatLng(44.240166,-76.523437))
                .add(new LatLng(44.241219,-76.511355))
                .add(new LatLng(44.240358,-76.509413))
                .add(new LatLng(44.238836,-76.505905))
                .add(new LatLng(44.239812,-76.505948))
                .add(new LatLng(44.238920,-76.503952))
                .add(new LatLng(44.238958,-76.503351))
                .add(new LatLng(44.238551,-76.502021))
                .add(new LatLng(44.238720,-76.497933))
                .add(new LatLng(44.239197,-76.497976))
                .add(new LatLng(44.239274,-76.496506))
                .add(new LatLng(44.239043,-76.496463))
                .add(new LatLng(44.238151,-76.494349))
                .add(new LatLng(44.237966,-76.493662))
                .add(new LatLng(44.237566,-76.492729))
                .add(new LatLng(44.235383,-76.489553))
                .add(new LatLng(44.234841,-76.486699))
                .add(new LatLng(44.234820,-76.482682))
                .add(new LatLng(44.234543,-76.481631))
                .add(new LatLng(44.234220,-76.481309))
                .add(new LatLng(44.234112,-76.480912))
                .add(new LatLng(44.234058,-76.480655))
                .add(new LatLng(44.233966,-76.480580))
                .add(new LatLng(44.233814,-76.479593))
                .add(new LatLng(44.233874,-76.479181))
                .add(new LatLng(44.233905,-76.478976))
                .add(new LatLng(44.233851,-76.478795))
                .add(new LatLng(44.233667,-76.478634))
                .add(new LatLng(44.233384,-76.477190));

        queensBoundary = mMap.addPolyline(queensBoundaryPolyLine
            .color(Color.BLUE));

       double[] bllat = {44.220355, 44.221264, 44.224576, 44.223813, 44.224725, 44.225449, 44.227653,
               44.226679, 44.225250, 44.224528, 44.223522, 44.229975, 44.230088, 44.230081, 44.228578,
               44.228498, 44.228491, 44.228047, 44.227789, 44.227700, 44.227732, 44.228022, 44.229008,
               44.228843, 44.228082, 44.228845, 44.228902, 44.228995, 44.229276, 44.229394, 44.229377,
               44.229693, 44.228681, 44.228223, 44.227765, 44.227637, 44.226912, 44.226770, 44.226356,
               44.227219, 44.226975, 44.227729, 44.226595, 44.226315, 44.226039, 44.225501, 44.225376,
               44.224644, 44.224671, 44.224452, 44.225414, 44.227114, 44.226312, 44.225717, 44.224966,
               44.224838, 44.224116, 44.223397, 44.222783, 44.224435, 44.223787};

        double[] blLong = {-76.507071, -76.506446, -76.509949, -76.513372, -76.513421, -76.514355, -76.514087,
                -76.516331, -76.516608, -76.516004, -76.515209, -76.516415, -76.497494, -76.497933, -76.497471,
                -76.497948, -76.498151, -76.497865, -76.497740, -76.498158, -76.498021, -76.496618, -76.495883,
                -76.495386, -76.494686, -76.493141, -76.494458, -76.494284, -76.494703, -76.494104, -76.493692,
                -76.494316, -76.492179, -76.491696, -76.491683, -76.492972, -76.493336, -76.492542, -76.492495,
                -76.493992, -76.494643,  -76.495372, -76.494231, -76.494045, -76.494819, -76.494505, -76.495151,
                -76.494424, -76.493998, -76.495275, -76.492918, -76.490860, -76.491681, -76.491152, -76.490990,
                -76.491794, -76.491418, -76.491611, -76.491114, -76.493697, -76.495562};

        for(int i=0; i<61; i++){
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(bllat[i], blLong[i]))
                    .radius(20)
                    .strokeColor(Color.WHITE)
                    .fillColor(Color.BLUE));
        }

        //walkhome location
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.drawable.walkhomelogo2);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallIcon = Bitmap.createScaledBitmap(b, width, height, false);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(44.22838027067406, -76.49507761001587))
                .icon(BitmapDescriptorFactory.fromBitmap(smallIcon))
        );

        bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.drawable.campussecuritylogo);
        b=bitmapdraw.getBitmap();
        smallIcon = Bitmap.createScaledBitmap(b, width, height, false);
        //Campus Security Location
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(44.225315,-76.498425))
                .icon(BitmapDescriptorFactory.fromBitmap(smallIcon)));

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
                .target(newCurrentLatLng).zoom(14).build();

        //mMap.animateCamera(CameraUpdateFactory
                //.newCameraPosition(cameraPosition));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCurrentLatLng, 14));


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

    //Once we have all the required information, send it to walkhome api
    public void sendDataToWalkhome(){
        //phoneNumber = "9055456969";
        URL url = null;
        StringBuilder result = new StringBuilder();
        //String response = null;

        //gets the current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(calendar.getTime());

        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        time = mdformat.format(calendar.getTime()) + " " + time;

        String parameters = "function=addWalk&team=w1&request_time=" + time + "&status=1&pick_up_location=" + currentAddressFrom +
                "&drop_off_location=" + currentAddressTo + "&phone_number=" + phoneNumber;
        try{
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://www.backstage.compsawebservices.com/walkhome/api.php?"+parameters)
                    //.post(body)
                    .build();

            connection.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    System.out.println("CONNECTION RESPONSE: FAILED");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    System.out.println("CONNECTION RESPONSE: SUCCESS" + response);
                }
            });
    } catch (Exception error){

        }//end catch
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
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        if (id == R.id.about_walkhome) {
            // Handle the camera action
            Intent startActivityInformation = new Intent(NavigationActivity.this, InformationActivity.class);
            startActivity(startActivityInformation);
        } else if (id == R.id.about_campus_security) {
            System.out.println("phonenumber: " + phoneNumber);

        } else if (id == R.id.request_walk) {
            //setCurrentLocation();
            //resets the flag after user tries to request a new walk
            flag = false;
            Intent currentLocationIntent = new Intent(NavigationActivity.this, DirectionActivity.class);
            Bundle setBundle = new Bundle();
            setBundle.putDouble("currentLat", currentLat);
            setBundle.putDouble("currentLong", currentLong);

            try {
                List<Address> addresses = geocoder.getFromLocation(currentLat, currentLong, 1);
                String address = addresses.get(0).getAddressLine(0);
                setBundle.putString("current_address", address);
            } catch(Exception e){

            }
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
