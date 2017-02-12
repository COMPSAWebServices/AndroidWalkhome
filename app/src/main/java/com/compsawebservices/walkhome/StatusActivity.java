package com.compsawebservices.walkhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * StatusActivity class handles http request from COMPSA backend api to get the current walk status.
 * It first uses the phone number to get the current walk state and updates the page accordingly.
 * Once status = 4 which means the walk has been completed, redirect the page to FeedbackActivity.
 * Author: Ly Sung
 * Date: Dec 11th 2016
 * Version: 3.0
 * **/
public class StatusActivity extends AppCompatActivity {
    private  TextView reqSent;
    private  TextView reqReceived;
    private  TextView walkerOut;
    private  TextView walkProgress;
    private  TextView walkCompleted;
    private  TextView statusInfo;

    private Button cancelWalk;
    private Button callWalkhome;
    private Button feedbackForm;
    static StatusTracker st = new StatusTracker();
    //will be using userProfile to keep track of walk status
    static UserProfile userProfile = new UserProfile();
    private String walkID;
    private String walkStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));

        getSupportActionBar().setTitle("Status");

        statusUpdate();

        reqSent = (TextView)findViewById(R.id.request_sent);
        reqReceived = (TextView)findViewById(R.id.request_received);
        walkerOut = (TextView)findViewById(R.id.walker_out);
        walkProgress = (TextView)findViewById(R.id.walk_in_progress);
        walkCompleted = (TextView)findViewById(R.id.walk_completed);
        statusInfo = (TextView)findViewById(R.id.status_info);

        cancelWalk = (Button)findViewById(R.id.button_cancel_request);
        callWalkhome = (Button)findViewById(R.id.button_call_walkhome);
        feedbackForm = (Button)findViewById(R.id.button_feedback_form);

        //call walk home button
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

        //feedback button
        feedbackForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        //cancel button
        cancelWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancels walk
                String parameters = "function=getWalkByUserPhoneNumber&phone_number="+ userProfile.getPhonenumber();
                try{
                    OkHttpClient connection = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://backstage.compsawebservices.com/walkhome/api.php?"+parameters)
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
                                            .url("http://backstage.compsawebservices.com/walkhome/api.php?"+parameters2)
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
                } catch (Exception error){}
                //resets the counter
                //st.resetCount();
            }//end onCLICK
        });//end cancelwalk

        //checks and updates the current status
        statusUpdate();
        /*statusPageUpdate was running before statusUpdate()
        * Added a delay so that statusUpdate() finishes first*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                statusPageUpdate();
            }
        }, 500);

    }//end on create

    /**Calls the backend to check what status the walk is currently in and updates the status*/
    public void statusUpdate(){
        String parameters2 = "function=getWalkByUserPhoneNumber&phone_number="+ userProfile.getPhonenumber();
        try{
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://backstage.compsawebservices.com/walkhome/api.php?"+parameters2)
                    //.post(body)
                    .build();
            connection.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    System.out.println("CONNECTION RESPONSE: FAILED");
                }
                //if there is a reponse
                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        walkStatus = obj.getJSONObject("walk").getString("status");
                        int currentStatus = Integer.parseInt(walkStatus);
                        userProfile.setCurrentStatus(currentStatus);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });//end on respond
        } catch (Exception error){}
    }//end statusupdate

    /**Updates the page by checking with the walk status saved in UserProfile**/
    public void statusPageUpdate(){
        if (userProfile.getCurrentStatus() == 1) {
            reqReceived.setTextColor(Color.WHITE);
            walkerOut.setTextColor(Color.parseColor("#818181"));
            walkProgress.setTextColor(Color.parseColor("#818181"));
            walkCompleted.setTextColor(Color.parseColor("#818181"));
            statusInfo.setText("Your request has been received. The next available walking team will be heading your way");
        } else if (userProfile.getCurrentStatus() == 2) {
            reqReceived.setTextColor(Color.WHITE);
            walkerOut.setTextColor(Color.WHITE);
            walkProgress.setTextColor(Color.parseColor("#818181"));
            walkCompleted.setTextColor(Color.parseColor("#818181"));
            statusInfo.setText("Walkers are currently on their way!");
            //walkers out
        } else if (userProfile.getCurrentStatus() == 3) {
            reqReceived.setTextColor(Color.WHITE);
            walkerOut.setTextColor(Color.WHITE);
            walkProgress.setTextColor(Color.WHITE);
            walkCompleted.setTextColor(Color.parseColor("#818181"));
            statusInfo.setText("Walk in progress...");
        } else if (userProfile.getCurrentStatus() == 4) {
            reqReceived.setTextColor(Color.WHITE);
            walkerOut.setTextColor(Color.WHITE);
            walkProgress.setTextColor(Color.WHITE);
            walkCompleted.setTextColor(Color.WHITE);
            statusInfo.setText("Walk completed!");
            Intent i = new Intent(StatusActivity.this, FeedbackActivity.class);
            startActivity(i);
        } else{
            //statusInfo.setText("There is no walk on this phone number!");
            Intent i = new Intent(StatusActivity.this, MainActivity.class);
            startActivity(i);
        }
    }//end statusPageUpdate

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}//end StatusActivity class