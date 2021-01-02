package com.sh.womansafetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Handler handler = new Handler();



        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                    Intent i = new Intent(getApplicationContext()  , Home.class);
                    startActivity(i);
                    finish();
                }
                else {
                    Intent oi = new Intent(getApplicationContext() , LogInActivity.class);

                    startActivity(oi);
                    finish();
                }

            }
        }, 1500) ;
    }
}
