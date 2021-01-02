package com.sh.womansafetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Locale;

public class Home extends AppCompatActivity {


    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private  double Lat;
    private  double Lon;
    private static final int PERMISSION_SEND_SMS = 123;

    CardView scream,fakecall,location,send_location,friends,setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /////////////////////

        ////////////////////

        scream=findViewById(R.id.scream);
        fakecall=findViewById(R.id.fake);
        location=findViewById(R.id.location);
        send_location=findViewById(R.id.send_location);
        friends=findViewById(R.id.frnd);
        setting=findViewById(R.id.setting);


        //hit and show navigation from me to destination
        /*Uri gmmIntentUri = Uri.parse("google.navigation:q=28.5675,77.3260");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent); */

        final MediaPlayer mp= MediaPlayer.create(getApplicationContext(),R.raw.siren);


        scream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mp.setLooping(true);
                mp.start();


                new AlertDialog.Builder(Home.this).setTitle("Scream Alert").setMessage("Click on stop below to stop the screaming")
                        .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mp.pause();
                            }
                        })
                        .setCancelable(false).setIcon(getResources().getDrawable(R.drawable.ic_security_black_24dp)).show();



            }
        });

        fakecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Fakecall.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             call();

            }
        });


        //////////////////





        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Rescuer.class));
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Settings.class));
            }
        });


        send_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {


                    requestSmsPermission();

                }else {


                    new AlertDialog.Builder(Home.this).setIcon(getResources().getDrawable(R.drawable.ic_security_black_24dp))
                            .setTitle("Location to Rescuers").setMessage("This will send your current Location to all of your rescuers.")
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Rescuers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                                                Rescuer_Model rescuer_model=dataSnapshot1.getValue(Rescuer_Model.class);

                                                String a=rescuer_model.getName()+" I am at risk please track me. My Current Location: "+"https://www.google.com/maps/search/?api=1&query=" + Lat + "," + Lon + "&travelmode=driving";
                                                sendSms(rescuer_model.getPhone(),a);;

                                            }
                                            Toast.makeText(getApplicationContext(),"Location Sent to Rescuers",Toast.LENGTH_LONG).show();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }

            }
        });



    }


    private void call() {


      //  geocoder = new Geocoder(this, Locale.ENGLISH);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {

                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Lat = location.getLatitude();
                    Lon = location.getLongitude();


                    String url = "https://www.google.com/maps/search/?api=1&query=" + Lat + "," + Lon + "&travelmode=driving";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(),Lat+" "+Lon,Toast.LENGTH_SHORT).show();



                }
            }
        };

        // fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();


    }


    private void createLocationRequest(){

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},101);


            return;
        }


            fusedLocationClient= LocationServices.getFusedLocationProviderClient(Home.this);
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);


    }



    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
           // sendSms(phone, message);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                   // sendSms(phone, message);
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    private void sendSms(String phoneNumber, String message){
        Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }


}
