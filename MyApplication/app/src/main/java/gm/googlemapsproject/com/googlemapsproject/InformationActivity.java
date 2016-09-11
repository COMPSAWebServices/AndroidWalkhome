package gm.googlemapsproject.com.googlemapsproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //sets walkhome info text
        walkhomeInfo = (TextView)findViewById(R.id.walkhome_info);
        walkhomeInfo.setText(walkhomeInfoString);

        feedbackButton = (Button)findViewById(R.id.feedback_button);
        callWalkhome = (Button)findViewById(R.id.call_walkhome_button);
        callCampusSecurity = (Button)findViewById(R.id.call_campussecurity_button);

    }//end onCreate

}
