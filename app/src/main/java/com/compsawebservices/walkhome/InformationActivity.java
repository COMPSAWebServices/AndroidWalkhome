package com.compsawebservices.walkhome;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.compsawebservices.walkhome.R;

/**
 * Sets up walkhome info page
 * */
public class InformationActivity extends AppCompatActivity {
    private TextView walkhomeInfo;
    private String walkhomeInfoString = "Walkhome is a student-run safety service in Kingston, Ontario. We provide safe walks to students both on the Queen’s University Campus and within the Kingston community. We are a completely anonymous and confidential service, so our staff members do not wear any clothes identifying them as a Walkhome team. We are inclusive to all students on campus, no matter your year or faculty. When you request a walk, teams of one male and one female student will accompany you to a destination of your choosing. Whether you want enjoyable conversation, safe escort for your walk, or friends to accompany you, Walkhome is the place to call!\n" +
            "\n" +
            "Hours\n" +
            "School Year Hours\n" +
            "Sunday – Wednesday: Dusk – 2:00 am\n" +
            "Thursday – Saturday: Dusk – 3:00 am\n" +
            "\n" +
            "Summer Hours\n" +
            "Every Day, 9:00 pm – 1:00 am\n" +
            "Exam Hours\n" +
            "Sunday – Saturday: 8:00 pm – 4:00 am\n" +
            "\n" +
            "If you are looking for our office, we can be found in JDUC 044. If you are in theLower Ceilidh, facing the doors to the P&CC, take the hallway to your right. Take the first left, then the first right. We are the first office on the left from there. Feel free to stop by during our office hours to ask any questions!";
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

        //make walkhomeInfo scrollable
        walkhomeInfo.setMovementMethod(new ScrollingMovementMethod());


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
