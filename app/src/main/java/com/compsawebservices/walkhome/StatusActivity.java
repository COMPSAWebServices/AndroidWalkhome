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

//    Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));

        getSupportActionBar().setTitle("Status");

        reqSent = (TextView)findViewById(R.id.request_received);
        reqReceived = (TextView)findViewById(R.id.request_sent);
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
                callWalkHome.setData(Uri.parse("tel:9057589989"));
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

        //getting changes
//        mRef = new Firebase("https://walkhome-68dbb.firebaseio.com");
//
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //data changes
//                String data = dataSnapshot.getValue(String.class);
//                statusInfo.setText(data);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

    }//end on create

//    @Override
//    protected void onStart() {
//        super.onStart();
//        x
//    }//end onStart()
}