package com.compsawebservices.walkhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.firebase.client.ValueEventListener;

public class StatusActivity extends AppCompatActivity {
    private TextView reqSent;
    private TextView reqReceived;
    private TextView walkerOut;
    private TextView walkProgress;
    private TextView walkCompleted;
    private TextView statusInfo;

    private Button cancelWalk;
    private Button callWalkhome;
    private Button feedbackForm;
    private int statusIncrementor;
    private int status;
    static StatusTracker st = new StatusTracker();
    static UserProfile userProfile = new UserProfile();
    private String walkID;


//    Firebase mRef;

//    public StatusActivity(){
//        this.status = 0;
//        this.reqSent = (TextView)findViewById(R.id.request_received);
//        this.reqReceived = (TextView)findViewById(R.id.request_sent);
//        this.walkerOut = (TextView)findViewById(R.id.walker_out);
//        this.walkProgress = (TextView)findViewById(R.id.walk_in_progress);
//        this.walkCompleted = (TextView)findViewById(R.id.walk_completed);
//        this.statusInfo = (TextView)findViewById(R.id.status_info);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));

        getSupportActionBar().setTitle("Status");

        reqSent = (TextView)findViewById(R.id.request_sent);
        reqReceived = (TextView)findViewById(R.id.request_received);
        walkerOut = (TextView)findViewById(R.id.walker_out);
        walkProgress = (TextView)findViewById(R.id.walk_in_progress);
        walkCompleted = (TextView)findViewById(R.id.walk_completed);
        statusInfo = (TextView)findViewById(R.id.status_info);

        cancelWalk = (Button)findViewById(R.id.button_cancel_request);
        callWalkhome = (Button)findViewById(R.id.button_call_walkhome);
        feedbackForm = (Button)findViewById(R.id.button_feedback_form);


        callWalkhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callWalkHome = new Intent(Intent.ACTION_DIAL);
                callWalkHome.setData(Uri.parse("tel:6135339255"));
                if (ActivityCompat.checkSelfPermission(StatusActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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


        feedbackForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        cancelWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancels walk
                String parameters = "function=getWalkByUserPhoneNumber&phone_number="+ userProfile.getPhonenumber();
                try{
                    OkHttpClient connection = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://dev.compsawebservices.com/walkhome/api.php?"+parameters)
                            //.post(body)
                            .build();

                    connection.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            System.out.println("CONNECTION RESPONSE: FAILED");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String jsonData = response.body().string();

                            try {
                                JSONObject obj = new JSONObject(jsonData);
                                walkID = obj.getJSONObject("walk").getString("id");
//                                JSONArray walkIDJson = (JSONArray) obj.get("walk");
//                                ArrayList<String> list = new ArrayList<String>();
//                                for(int i=0; i<walkIDJson.length(); i++){
//                                    list.add(walkIDJson.getJSONObject(i).getString("name"));
//                                }
//
//                                System.out.println("ARRAYLIST" + list);
//                                System.out.println("JSONARRAY" + walkIDJson);
                                System.out.println("CONNECTION RESPONSE: SUCCESS ID" +walkID);

                                //call cancel walk function here
                                String parameters2 = "function=cancelWalk&id="+ walkID;
                                try{
                                    OkHttpClient connection = new OkHttpClient();
                                    final Request request = new Request.Builder()
                                            .url("http://dev.compsawebservices.com/walkhome/api.php?"+parameters2)
                                            //.post(body)
                                            .build();

                                    connection.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {
                                            System.out.println("CONNECTION RESPONSE: FAILED");
                                        }

                                        @Override
                                        public void onResponse(Response response) throws IOException {
                                            System.out.println("CONNECTION RESPONSE: WALK DELETED" + response);
                                        }
                                    });
                                } catch (Exception error){

                                }//end cancelwalk catch




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            System.out.println("CONNECTION RESPONSE: SUCCESS " + response.body().string());

                            //redirect back to navigation
                            Intent intent = new Intent(StatusActivity.this, NavigationActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (Exception error){

                }
                //resets the counter
                st.resetCount();
            }//end onCLICK
        });//end cancelwalk

        //get intent
        Intent intent = getIntent();
        statusIncrementor = intent.getIntExtra("status", 0);
        //StatusTracker st = new StatusTracker();
        //st.updateCount();
//        status = statusIncrementor;
        status = st.getCount();

        switch(status){
            case 1:
                reqReceived.setTextColor(Color.WHITE);
                statusInfo.setText("Your request has been received. The next available walking team will be heading your way");
                break;
            case 2:
                reqReceived.setTextColor(Color.WHITE);
                walkerOut.setTextColor(Color.WHITE);
                statusInfo.setText("Walkers are currently on their way!");
                break;
            case 3:
                reqReceived.setTextColor(Color.WHITE);
                walkerOut.setTextColor(Color.WHITE);
                walkProgress.setTextColor(Color.WHITE);
                statusInfo.setText("Walk in progress...");
                break;
            case 4:
                reqReceived.setTextColor(Color.WHITE);
                walkerOut.setTextColor(Color.WHITE);
                walkProgress.setTextColor(Color.WHITE);
                walkCompleted.setTextColor(Color.WHITE);
                statusInfo.setText("Walk completed!");

                //disables cancel walk button
                cancelWalk.setEnabled(false);
                Intent i = new Intent(StatusActivity.this, FeedbackActivity.class);
                startActivity(i);
                break;

        }

        //cancelWalk


    }//end on create


    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        st.resetCount();
    }

}