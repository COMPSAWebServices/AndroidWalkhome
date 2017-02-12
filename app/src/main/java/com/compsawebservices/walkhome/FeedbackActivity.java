package com.compsawebservices.walkhome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.compsawebservices.walkhome.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Sets up feedback page
 * */
public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackText;
    private String userFeedback;
    static UserProfile userProfile = new UserProfile();
    private Toast toast;
    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));

        getSupportActionBar().setTitle("FeedBack");

        //displays the back  button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ToggleButton Btn= new ToggleButton(this);// or get it from the layout by ToggleButton Btn=(ToggleButton) findViewById(R.id.IDofButton);
        Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked)
                    buttonView.setBackgroundColor(Color.GREEN);
                else buttonView.setBackgroundColor(Color.RED);
            }
        });

        feedbackText = (EditText)findViewById(R.id.feedback_editText);
        userFeedback = feedbackText.getText().toString();
        //gets the current time

        submit = (Button)findViewById(R.id.submit_feedback) ;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String time = sdf.format(calendar.getTime());

                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                time = mdformat.format(calendar.getTime()) + " " + time;
                String parameters = "function=feedback&message="+ userFeedback + "&phone_number="+ userProfile.getPhonenumber() + "&time=" + time;
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
                            System.out.println("CONNECTION RESPONSE: SUCCESS" + response);

                            if(userProfile.getCurrentStatus() == 4){
                                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(FeedbackActivity.this, StatusActivity.class);
                                startActivity(intent);
                            }

                        }//end onReponse
                    });
                } catch (Exception error){

                }//end catch
            }
        });//end setOnCLickLlistener
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