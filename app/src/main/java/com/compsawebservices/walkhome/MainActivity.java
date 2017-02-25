package com.compsawebservices.walkhome;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Description: Asks the user to enter their phone number and two things can happen
 *  1) There is an active walk with the phone number. In this case, direct the user to StatusActivity page.
 *  2) There is no active walk, then direct the user to NavigationActivity where they can request a walk.
 *
 *  To check if there is an active walk:
 *  1. createUser by passing in the phonenumber and firebase generated ID
 *  2. call getWalkByUserPhoneNumber to check if there is an active walk and the status of the walk.
 * **/
public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText phonenumber;
    private String phonenumberString;
    private Long phonenumberLong;
    static UserProfile up;
    private String walkStatus;
    private String active;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Walkhome");

        //firebase instance ID provides a unique identifier for each app instance
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

        phonenumber = (EditText) findViewById(R.id.user_phone_number);
        loginButton = (Button)findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumberString = phonenumber.getText().toString().replace(" ","".replace("-", ""));
                //Checks that the phone number entered is valid before login
                if (!phoneNumberVerification(phonenumberString)){
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,"Please provide a valid phone number.",duration);
                    toast.show();
                }else{
                    //walkhome createUser
                    String parameters = "function=createUser&phone=" + phonenumberString + "&device_token=" + FirebaseInstanceId.getInstance().getToken();
                    try {
                        OkHttpClient connection = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://backstage.compsawebservices.com/walkhome/api.php?" + parameters)
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
                    } catch (Exception error) {}//end catch

                    //checks if there is a walk
                    String parameters2 = "function=getWalkByUserPhoneNumber&phone_number=" + phonenumberString;
                    try {
                        OkHttpClient connection = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://backstage.compsawebservices.com/walkhome/api.php?" + parameters2)
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

                                //gets the walk status
                                try {
                                    JSONObject obj = new JSONObject(jsonData);
                                    walkStatus = obj.getJSONObject("walk").getString("status");
                                    active = obj.getJSONObject("walk").getString("active");
                                    //there is an active walk
                                    if (active.equals("1")) {
                                        int status = Integer.parseInt(walkStatus);
                                        //if the status is 4 (completed) then redirect the user to NavigationActivity
                                        //else redirect them to statusActivity
                                        if (status!=4){
                                            Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                            startActivity(intent);
                                        }
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } //end getWalkByPhoneNumber Try
                    catch (Exception error) {}

                    //if there is no active walk just go to navigationactivity
                    if (active == null) {
                        Intent loginIntent = new Intent(MainActivity.this, NavigationActivity.class);
                        up = new UserProfile();
                        up.updatePhonenumber(phonenumberString);
                        startActivity(loginIntent);
                    }
            }//end else
            }
        });
    }//end onCreate

    /*Checks that the phone is the correct format*/
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
    }//end phoneNumberVerification

}
