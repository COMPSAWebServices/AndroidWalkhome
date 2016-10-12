package com.compsawebservices.walkhome;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.compsawebservices.walkhome.R;

public class InformationActivity extends AppCompatActivity {
    private TextView walkhomeInfo;
    private String walkhomeInfoString = "Walkhome is a student-run safety service in Kingston, Ontario. We provide safe walks to students both on" +
            " the Queen's University Campus and within the Kingston community. We are a completely anonymous and confidential service, so " +
            "our staff members do not wear any clothes identifying them as a Walkhome team. We are inclusive to all students on campus, " +
            "no matter your year or faculty. When you request a walk, teams of one male and one female student will.";
    private Button feedbackButton;
    private Button callWalkhome;
    private Button callCampusSecurity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));

        getSupportActionBar().setTitle("WalkHome Information");

        //displays the back  button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //sets walkhome info text
        walkhomeInfo = (TextView) findViewById(R.id.walkhome_info);
        walkhomeInfo.setText(walkhomeInfoString);

        feedbackButton = (Button) findViewById(R.id.feedback_button);
        callWalkhome = (Button) findViewById(R.id.call_walkhome_button);
        callCampusSecurity = (Button) findViewById(R.id.call_campussecurity_button);

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startActivityInformation = new Intent(InformationActivity.this, FeedbackActivity.class);
                startActivity(startActivityInformation);
            }
        });

        callWalkhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callWalkHome = new Intent(Intent.ACTION_DIAL);
                callWalkHome.setData(Uri.parse("tel:6135339255"));
                if (ActivityCompat.checkSelfPermission(InformationActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

        callCampusSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callCampusSecurity = new Intent(Intent.ACTION_DIAL);
                callCampusSecurity.setData(Uri.parse("tel:6135536111"));
                if (ActivityCompat.checkSelfPermission(InformationActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callCampusSecurity);
            }
        });//end callCW onclicklistener

    }//end onCreate

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
