package com.example.csprecorder;

import java.io.IOException;
import java.nio.file.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    ImageButton btnRec;
    Button btnstop;
    TextView txtRecStatus;
    Chronometer timeRec;
    double longitu,latitu;
    LocationCallback locationCallback;
    String[] appPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.MANAGE_EXTERNAL_STORAGE};
    static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC; // for raw audio
    static final int SAMPLE_RATE = 44100;
    static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    static final int BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    protected AudioRecord audioRecord;

    private static String fileName;
    private MediaRecorder recorder;
    boolean isRecording;

    private WavAudioRecorder mRecorder;

    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder");  // Path to save the recordings temporary



    private static final int PERMISSION_FINE_LOCATION = 99;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    TextView lat,lon;
    Context context = this;

    //Google Api for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //System.out.println(path);
//        if(!path.exists()){
//            System.out.println("Inside");
//            path.mkdir();
//        }
        path.mkdirs();
//        if(path.exists()){
//            System.out.println("Inside");
//        }
//        System.out.println("Oncreate");
//
//        File directory = new File(Environment.getExternalStorageDirectory() + java.io.File.separator +"Directory");
//        if (!directory.exists()) {
//            directory.exists();
//            System.out.println("Created");
//        }
//        else
//            System.out.println("Not created");
//        System.out.println(directory);
//        if(directory.exists()){
//            System.out.println("It really created");
//        }

        // Activating app to run the python file
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python py = Python.getInstance();

        // now create python object
        PyObject pyobj = py.getModule("myscript"); // python file with FFT and energy program

        btnRec = findViewById(R.id.btnRec);             // Recording button
        btnstop = findViewById(R.id.btnstop);           // Stop button

        lat=findViewById(R.id.lat);             // Latitude
        lon=findViewById(R.id.longi);           // Longitude
        txtRecStatus = findViewById(R.id.txtRecStatus);

        timeRec = findViewById(R.id.timerec);    // Recording timer

        isRecording = false;
        checkForPermissions();


        if(!path.exists()){
            path.mkdirs();
        }
        ////create new instance of class



        locationRequest = LocationRequest.create()
                .setInterval(1000)          //update the location every 1000 ms through setInterval
                .setFastestInterval(1000)   //get location update every 1 second
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)   //Use GPS to get location
                .setMaxWaitTime(100);
        //event that is triggered whenever the update interval is met
        locationCallback=new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location=locationResult.getLastLocation();
                //update location latitude and longitude view
                send_location(location);
            }
        };


        // Setting the action on clicking the recording button
        btnRec.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btnRec.setEnabled(false);
                btnRec.setVisibility(View.GONE);
                SimpleDateFormat format=new SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault());;
                String date=format.format(new Date());

                Timer timer = new Timer();

                // This loop will run for 19000 ms
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

//                        SimpleDateFormat format=new SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault());;
//                        String date=format.format(new Date());
//                        try {
                        SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyy_HH_mm_ss", Locale.getDefault());
                        String date = format.format(new Date());



                        fileName = path + "/recording " + date + ".wav";

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeRec.setBase(SystemClock.elapsedRealtime());      // Recording timer is
                                timeRec.start();                                     // Started
                                updateGPS();            // To update device location
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                mRecorder = WavAudioRecorder.getInstanse();
                                mRecorder.setOutputFile(fileName);

                                System.out.println("Preparation");
                                mRecorder.prepare();
                                mRecorder.start();                      // Recording is started
                                System.out.println("Recording started");

                                Handler handler = new Handler();

                                // postDelayed will run this handler for 10000 ms. Hence, we will get a recording of 100000 ms.
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("Timer finished");
                                        mRecorder.release();
                                        mRecorder.stop();               // Recording stopped
                                        //Toast.makeText(MainActivity.this,"Exported",Toast.LENGTH_LONG).show();
                                        timeRec.setBase(SystemClock.elapsedRealtime());
                                        timeRec.stop();                 // Timer stopped
                                        txtRecStatus.setText("");

                                        //now call this function
                                        System.out.println("FileName "+ fileName);
                                        PyObject obj = pyobj.callAttr("main", fileName);
                                        List x = obj.asList();          // Receiving
//                                        System.out.println(obj);
//                                        PyObject top_20 = obj.get(0);
//                                        System.out.println(x.get(0));
                                        System.out.println("Hello");
                                        System.out.println(x.getClass().getName());
//                                        double x1 = x.get(0).as;


                                        new Writer(x.get(0), x.get(1), x.get(2), x.get(3), x.get(4), x.get(5), x.get(6), x.get(7), x.get(8), x.get(9), x.get(10), x.get(11), latitu, longitu, date, context);

                                        File myDir = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
                                        if (myDir.isDirectory()) {
                                            String[] children = myDir.list();
                                            for (int i = 0; i < children.length; i++) {
                                                new File(myDir, children[i]).delete();          // Delete the existing recording file
                                            }
                                        }
//                                        Toast.makeText(this,"Exported",Toast.LENGTH_LONG).show();
                                    }
                                }, 10000);
                            }
                        });


                        // On clicking stop button every process of this app will stop
                        btnstop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnRec.setEnabled(true);
                                btnRec.setVisibility(View.VISIBLE);
                                System.out.println("Process stopped!!");
                                Toast.makeText(MainActivity.this,"Process stopped!!",Toast.LENGTH_LONG).show();
                                //stopService(new Intent(MainActivity.this,MyService.class));
                                timeRec.setBase(SystemClock.elapsedRealtime());
                                timeRec.stop();
                                txtRecStatus.setText("");
                                timer.cancel();
                            }
                        });
                    }
                }, 0, 19000);

            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorder.stop();
                mRecorder.reset();
                System.out.println("Process stopped!!");
                Toast.makeText(MainActivity.this,"Process stopped!!",Toast.LENGTH_LONG).show();
                //stopService(new Intent(MainActivity.this,MyService.class));
                timeRec.setBase(SystemClock.elapsedRealtime());
                timeRec.stop();
                txtRecStatus.setText("");
            }
        });
    }

   //check for the all the permissions containing in the string appPermissions
    private void checkForPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        for(String per:appPermissions){
            if(ContextCompat.checkSelfPermission(this,per)!=PackageManager.PERMISSION_GRANTED){
                //if permission is not provided add in permissionNeeded
                permissionsNeeded.add(per);
            }
        }
        if(!permissionsNeeded.isEmpty()){
            //if any permission is not provided
            ActivityCompat.requestPermissions(this,permissionsNeeded.toArray(new String[permissionsNeeded.size()]),123);
        }
    }
   //updateGPS function to update GPS
    private void updateGPS()
    {
        //get permission from user to track GPS
        //get the current location from fused client
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //user provided the permission
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //we got the permission put the values in text views
                    send_location(location);
                }
            });
        }
        else
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINE_LOCATION);
            }
        }
    }
    //send_location function to update latitude and longitude
    private void send_location(Location location) {
        latitu=location.getLatitude();
        longitu=location.getLongitude();
        lat.setText(String.valueOf(location.getLatitude()));
        lon.setText(String.valueOf(location.getLongitude()));
    }
}