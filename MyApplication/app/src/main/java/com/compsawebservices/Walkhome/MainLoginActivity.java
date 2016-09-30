package com.compsawebservices.Walkhome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.compsawebservices.Walkhome.R;

public class MainLoginActivity extends AppCompatActivity {

    private EditText phonenumber;
    private Button loginButton;
    private String phonenumberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#1ca7f7"));


        getSupportActionBar().setTitle("Walkhome");

        phonenumber = (EditText) findViewById(R.id.user_phone_number);
        loginButton = (Button) findViewById(R.id.login_button);

        //gets the phone number entered


        System.out.println(phonenumberString);

        //phonenumber can't be empty
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumberString = phonenumber.getText().toString().replace(" ","".replace("-", ""));
                //TODO need to make sure its a phone number
                Intent loginIntent = new Intent(MainLoginActivity.this, NavigationActivity.class);

                /*
                Bundle setBundle = new Bundle();
                setBundle.putString("phonenumber", phonenumberString);
                setBundle.putString("page", "loginAct");


                loginIntent.putExtras(setBundle);
                */
                loginIntent.putExtra("phonenumber", phonenumberString);
                startActivityForResult(loginIntent, 111);
                finish();
        }
        });





    }//end oncreate
}
