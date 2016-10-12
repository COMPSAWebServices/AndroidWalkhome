package com.compsawebservices.walkhome;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText phonenumber;
    private String phonenumberString;
    private Long phonenumberLong;
    static UserProfile up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setTitle("Walkhome");
        int a = 0;

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();//llk

        System.out.println("IDDDDDDDDDDDDD:" +FirebaseInstanceId.getInstance().getToken());
        phonenumber = (EditText) findViewById(R.id.user_phone_number);
        loginButton = (Button)findViewById(R.id.login_button);
        //phonenumber can't be empty
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumberString = phonenumber.getText().toString().replace(" ","".replace("-", ""));

                if (!phoneNumberVerification(phonenumberString)){
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,"Please provide a valid phone number.",duration);
                }

                //verifying that inputted phone number could be a phone number

                String parameters = "function=createUser&phone="+ phonenumberString + "&device_token="+FirebaseInstanceId.getInstance().getToken();
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
                            System.out.println("CONNECTION RESPONSE: SUCCESS" + response);
                        }
                    });
                } catch (Exception error){

                }//end catch
                    Intent loginIntent = new Intent(MainActivity.this, NavigationActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("phonenumber", phonenumberString);
//                    loginIntent.putExtras(bundle);
//                   startActivityForResult(loginIntent, 111);
                    startActivity(loginIntent);
//                    finish();

                up = new UserProfile();
                up.updatePhonenumber(phonenumberString);
            }
        });
    }



    private boolean phoneNumberVerification(String phonenumber){
        try{
            phonenumberLong.parseLong(phonenumber,10);
        }catch (Exception e){
            return false;
        }
        if (phonenumber.length() != 10){
            return false;
        }
        return true;
    }
}
