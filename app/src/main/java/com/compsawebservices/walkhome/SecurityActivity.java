package com.compsawebservices.walkhome;

import android.*;
import android.Manifest;
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

/**
 * Created by Christian on 2016-10-11.
 */

public class SecurityActivity extends AppCompatActivity{
    private TextView securityInfo;
    private String securityInfoString = "The mandate of Campus Security and Emergency Services is to" +
            " promote a safe and welcoming environment that recognizes and is respectful of the " +
            "diverse nature of the Queen's Community. We will respect requests for confidentiality, " +
            "however please note that we have an obligation to respond to situations that may " +
            "threaten the safety of community members.The responsibility for security is shared by " +
            "every member of the Queen's community. This web site is designed to provide you with " +
            "the information you need to make informed decisions about your personal security. It " +
            "also provides information to faculties, schools, departments and units so that they may" +
            " select and implement appropriate security measures to safeguard their facilities," +
            " staff and students.";
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
        securityInfo = (TextView) findViewById(R.id.walkhome_info);
        securityInfo.setText(securityInfoString);

        feedbackButton = (Button) findViewById(R.id.feedback_button);
        callWalkhome = (Button) findViewById(R.id.call_walkhome_button);
        callCampusSecurity = (Button) findViewById(R.id.call_campussecurity_button);

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startActivityInformation = new Intent(SecurityActivity.this, FeedbackActivity.class);
                startActivity(startActivityInformation);
            }
        });
        callWalkhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callWalkHome = new Intent(Intent.ACTION_DIAL);
                callWalkHome.setData(Uri.parse("tel:9057589989"));
                if (ActivityCompat.checkSelfPermission(SecurityActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                callCampusSecurity.setData(Uri.parse("tel:9057589989"));
                if (ActivityCompat.checkSelfPermission(SecurityActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
