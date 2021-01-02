package com.sh.womansafetyapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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


public class ShakeService  extends Service implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity


    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private  double Lat;
    private  double Lon;
    private static final int PERMISSION_SEND_SMS = 123;
    boolean flag;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        call();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;

        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
       // mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        mAccelLast= mAccelCurrent;





        if (delta > 20) {
            // Random rnd = new Random();
            //  int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//            ServiceActivity.tvShakeService.setText("Service detects the Shake Action!! Color is also changed..!!!");
//            ServiceActivity.tvShakeService.setTextColor(color);

            if(!flag){

                flag=true;
                //Toast.makeText(getApplicationContext(),String.valueOf(delta),Toast.LENGTH_LONG).show();

                FirebaseDatabase.getInstance().getReference("Rescuers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            Rescuer_Model rescuer_model=dataSnapshot1.getValue(Rescuer_Model.class);

                            String a=rescuer_model.getName()+""+" I am at risk please track me. My Current Location: "+"https://www.google.com/maps/search/?api=1&query=" + Lat + "," + Lon + "&travelmode=driving";
                            sendSms(rescuer_model.getPhone(),a);
                           // Toast.makeText(getApplicationContext(),String.valueOf(Lat),Toast.LENGTH_LONG).show();


                        }
                        Toast.makeText(getApplicationContext(),"Location Sent to Rescuers",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



          /*      new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flag=false;
                    }
                },10000);  */

            }


        }

        }


    @Override
    public void onDestroy() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager = null;
        }

        super.onDestroy();
    }
    private void sendSms(String phoneNumber, String message){
       // Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
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




        fusedLocationClient= LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);


    }






}
