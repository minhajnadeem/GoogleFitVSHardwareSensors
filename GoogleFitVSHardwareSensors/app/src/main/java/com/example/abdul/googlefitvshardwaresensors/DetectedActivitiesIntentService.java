/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.abdul.googlefitvshardwaresensors;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DetectedActivitiesIntentService extends IntentService implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor aSensor;
    float[] accdata;
    private Sensor gSensor;
    float[] gyrdata;
    private Sensor mSensor;
    float[] megdata;
    protected static final String TAG = "DetectedActivitiesIS";
    String detected_Activities;
    String android_id;
    String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //sensorregisteration();
        Toast.makeText(this, "In start service", Toast.LENGTH_LONG).show();
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        this.aSensor = this.mSensorManager.getDefaultSensor(1);
        this.mSensor = this.mSensorManager.getDefaultSensor(2);
        this.gSensor = this.mSensorManager.getDefaultSensor(4);
        sensorregisteration();

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
        String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        // Broadcast the list of detected activities.
        localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        localIntent.putExtra(Constants.EXTRA_ACC_DATA,accdata);
        localIntent.putExtra(Constants.EXTRA_GYR_DATA,gyrdata);
        localIntent.putExtra(Constants.EXTRA_MEG_DATA,megdata);

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da : detectedActivities) {
            Log.i(TAG, Constants.getActivityString(
                    getApplicationContext(),
                    da.getType()) + " " + da.getConfidence() + "%"
            );
            //this line was causing exception because of null sensor data
            //String a = System.currentTimeMillis() + ";" + accdata[0] + ";" + accdata[1] + ";" + accdata[2] + ";" + gyrdata[0] + ";" + gyrdata[1] + ";" + gyrdata[2] + ";" + megdata[0] + ";" + megdata[1] + ";" + megdata[2] + ";" + "\n";

            String a = System.currentTimeMillis() + ";";
            if (accdata != null){
                a += accdata[0] + ";" + accdata[1] + ";" + accdata[2] + ";";
            }
            if (gyrdata != null){
                a += gyrdata[0] + ";" + gyrdata[1] + ";" + gyrdata[2] + ";";
            }
            if (megdata != null){
                a += megdata[0] + ";" + megdata[1] + ";" + megdata[2] + ";";
            }
            a += "\n";

            android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            detected_Activities = Constants.getActivityString(getApplicationContext(), da.getType()) + "," + da.getConfidence() + "%" + a;
            try {
                String dir = Environment.getExternalStorageDirectory() + File.separator + "Activity Recognition dataset";
                //create folder
                File folder = new File(dir); //folder name
                folder.mkdirs();
                //create file
                File file = new File(dir, "Activity Recognition.txt");
                FileWriter fw = new FileWriter(file, true);
                //BufferedWriter writer give better performance
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                pw.write(android_id + "," + "," + ts + detected_Activities + "," + mydate);
                pw.println("");
                //Closing BufferedWriter Stream
                bw.close();
                System.out.println("Data successfully appended at the end of file");
            } catch (IOException ioe) {
                System.out.println("Exception occurred:");
                ioe.printStackTrace();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    protected void sensordesrigester() {
        this.mSensorManager.unregisterListener(this);
    }

    public void sensorregisteration() {
        Toast.makeText(this, "service registration", Toast.LENGTH_LONG).show();

        this.mSensorManager.registerListener(this, this.aSensor, 0);
        this.mSensorManager.registerListener(this, this.mSensor, 0);
        this.mSensorManager.registerListener(this, this.gSensor, 0);
    }

    @Override
    public void onSensorChanged(SensorEvent paramSensorEvent) {
        if (paramSensorEvent.sensor.getType() == 1) {
            try {
                this.accdata = ((float[]) paramSensorEvent.values.clone());
                //return;
            } finally {
            }
        }
        if (paramSensorEvent.sensor.getType() == 4) {
            try {
                this.gyrdata = ((float[]) paramSensorEvent.values.clone());
                //return;
            } finally {
            }
        }
        if (paramSensorEvent.sensor.getType() == 2) {
            try {
                this.megdata = ((float[]) paramSensorEvent.values.clone());
                //return;
            } finally {
            }
        }
    }

}