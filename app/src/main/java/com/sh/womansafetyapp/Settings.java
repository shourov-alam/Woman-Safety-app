package com.sh.womansafetyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {


    CardView log,about;

    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        log=findViewById(R.id.logout);
        checkBox=findViewById(R.id.check);
        about=findViewById(R.id.about);


        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),About.class));
            }
        });



        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LogInActivity.class));
                finish();
            }
        });



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        if(preferences.contains("checked") && preferences.getBoolean("checked",false) == true) {
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);

        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox.isChecked()) {
                    startService(new Intent(getApplicationContext(),ShakeService.class));
                    editor.putBoolean("checked", true);
                    editor.apply();
                }else{
                    stopService(new Intent(getApplicationContext(),ShakeService.class));
                    editor.putBoolean("checked", false);
                    editor.apply();
                }
            }
        });





    }
}
