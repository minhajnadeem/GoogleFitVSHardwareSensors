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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdul.googlefitvshardwaresensors.adapters.UsersListAdapter;
import com.example.abdul.googlefitvshardwaresensors.interfaces.GetFeedbackInterface;
import com.example.abdul.googlefitvshardwaresensors.interfaces.GetUsersInterface;
import com.example.abdul.googlefitvshardwaresensors.interfaces.LogoutInterface;
import com.example.abdul.googlefitvshardwaresensors.interfaces.SendActivityDataInterface;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.example.abdul.googlefitvshardwaresensors.tasks.GetFeedbackTask;
import com.example.abdul.googlefitvshardwaresensors.tasks.GetUsersTask;
import com.example.abdul.googlefitvshardwaresensors.tasks.LogoutTask;
import com.example.abdul.googlefitvshardwaresensors.tasks.SendActivityDataTask;
import com.example.abdul.googlefitvshardwaresensors.utils.Prefs;
import com.example.abdul.googlefitvshardwaresensors.utils.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This sample demonstrates use of the
 * {@link com.google.android.gms.location.ActivityRecognitionApi} to recognize a user's current
 * activity, such as walking, driving, or standing still. It uses an
 * {@link android.app.IntentService} to broadcast detected activities through a
 * {@link BroadcastReceiver}. See the {@link DetectedActivity} class for a list of DetectedActivity
 * types.
 * <p/>
 * Note that this activity implements
 * {@link ResultCallback<R extends com.google.android.gms.common.api.Result>}.
 * Requesting activity detection updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}
 * and stopping updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates}
 * returns a {@link com.google.android.gms.common.api.PendingResult}, whose result
 * object is processed by the {@code onResult} callback.
 */
public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "MainActivity";

    /**
     * A receiver for DetectedActivity objects broadcast by the
     * {@code ActivityDetectionIntentService}.
     */
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    // UI elements.
    private Button mRequestActivityUpdatesButton;
    private Button mRemoveActivityUpdatesButton;
    private ListView mDetectedActivitiesListView;

    /**
     * Adapter backed by a list of DetectedActivity objects.
     */
    private DetectedActivitiesAdapter mAdapter;

    /**
     * The DetectedActivities that we track in this sample. We use this for initializing the
     * {@code DetectedActivitiesAdapter}. We also use this for persisting state in
     * {@code onSaveInstanceState()} and restoring it in {@code onCreate()}. This ensures that each
     * activity is displayed with the correct confidence level upon orientation changes.
     */
    private ArrayList<DetectedActivity> mDetectedActivities;


    //variables
    private Prefs prefs;
    private UserType userType;

    public enum UserType {
        DOCTOR(Constants.USER_TYPE_DOCTOR),
        CLIENT(Constants.USER_TYPE_CLIENT);

        private final int userType;

        UserType(int type) {
            this.userType = type;
        }

        public int getUserType() {
            return this.userType;
        }
    }

    private ArrayList<UserModel> userModelsList = new ArrayList<>();
    private ArrayList<DetectedActivity> mDetectedActivitiesData;
    private LogoutInterface logoutInterface;
    private GetUsersInterface getUsersInterface;
    private SendActivityDataInterface sendActivityDataInterface;
    private GetFeedbackInterface getFeedbackInterface;
    private Timer timer;

    //views
    private TextView tvTitle, tvFeedback, tvFeedbackBy;
    private ImageView ivLogout;
    private LinearLayout layoutDoctorView;
    private RelativeLayout layoutClientView;
    private RecyclerView rvUsersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        System.out.println("onCreate1 ");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        System.out.println("onCreate ");
        // Get the UI widgets.
        mRequestActivityUpdatesButton = (Button) findViewById(R.id.request_activity_updates_button);
        mRemoveActivityUpdatesButton = (Button) findViewById(R.id.remove_activity_updates_button);
        mDetectedActivitiesListView = (ListView) findViewById(R.id.detected_activities_listview);

        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        // Enable either the Request Updates button or the Remove Updates button depending on
        // whether activity updates have been requested.
        setButtonsEnabledState();

        // Reuse the value of mDetectedActivities from the bundle if possible. This maintains state
        // across device orientation changes. If mDetectedActivities is not stored in the bundle,
        // populate it with DetectedActivity objects whose confidence is set to 0. Doing this
        // ensures that the bar graphs for only only the most recently detected activities are
        // filled in.
        if (savedInstanceState != null && savedInstanceState.containsKey(
                Constants.DETECTED_ACTIVITIES)) {
            mDetectedActivities = (ArrayList<DetectedActivity>) savedInstanceState.getSerializable(
                    Constants.DETECTED_ACTIVITIES);
        } else {
            mDetectedActivities = new ArrayList<DetectedActivity>();

            // Set the confidence level of each monitored activity to zero.
            for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
                mDetectedActivities.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], 0));
            }
        }

        // Bind the adapter to the ListView responsible for display data for detected activities.
        mAdapter = new DetectedActivitiesAdapter(this, mDetectedActivities);
        mDetectedActivitiesListView.setAdapter(mAdapter);

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        prefs = new Prefs(this);
        timer = new Timer();
        userType = prefs.getInt(Constants.PREF_USER_TYPE, Constants.USER_TYPE_CLIENT) == Constants.USER_TYPE_CLIENT ? UserType.CLIENT : UserType.DOCTOR;

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.string_title_main_activity);

        tvFeedback = findViewById(R.id.tvFeedback);
        tvFeedbackBy = findViewById(R.id.tvFeedbackBy);

        ivLogout = findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutTask(logoutInterface).execute(
                        Constants.SERVER_URL + Constants.API_ENDPOINT_LOGOUT,
                        prefs.getValue(Constants.PREF_AUTH_TOKEN, "")
                );
            }
        });

        layoutClientView = findViewById(R.id.layoutClientView);
        layoutDoctorView = findViewById(R.id.layoutDoctorView);

        rvUsersList = findViewById(R.id.rvUsersList);
        rvUsersList.setLayoutManager(new LinearLayoutManager(this));
        setupUI();

        logoutInterface = new LogoutInterface() {
            @Override
            public void onLogoutResult(String error, boolean success) {
                if (error == null) {
                    prefs.clear();
                    Util.doAfterLogout(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        getUsersInterface = new GetUsersInterface() {
            @Override
            public void onGetUsersResult(String error, ArrayList<UserModel> userModelList) {
                if (error == null) {
                    if (userModelList != null) {
                        rvUsersList.setAdapter(new UsersListAdapter(MainActivity.this, userModelList));
                    } else {
                        Toast.makeText(MainActivity.this, Constants.ERROR, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        sendActivityDataInterface = new SendActivityDataInterface() {
            @Override
            public void onSendActivityDataResult() {

            }
        };

        getFeedbackInterface = new GetFeedbackInterface() {
            @Override
            public void onGetFeedbackResult(String error, String feedback, String doctorName) {
                if (error == null) {
                    tvFeedbackBy.setText(getString(R.string.string_feedback_by, doctorName));
                    tvFeedback.setText(feedback);
                }else {
                    Toast.makeText(MainActivity.this, Constants.ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        };

        new GetUsersTask(getUsersInterface).execute(
                Constants.SERVER_URL + Constants.API_ENDPOINT_GET_USERS
                , prefs.getValue(Constants.PREF_AUTH_TOKEN, ""));

        scheduleGetFeedback();
    }

    /**
     * make API call after every 5 sec to get feedback by doctor
     */
    private void scheduleGetFeedback() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new GetFeedbackTask(getFeedbackInterface).execute(
                        Constants.SERVER_URL + Constants.API_ENDPOINT_GET_FEEDBACK,
                        prefs.getInt(Constants.PREF_USER_ID, -1) + ""
                );
            }
        }, 1000, 5000);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        timer.cancel();
        timer.purge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
//        if (!UpdateService.ScreenReceiver.screenOff) {
//            // this
//            // is when onResume() is called due to a screen state change
//            System.out.println("SCREEN TURNED ON");
//            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//            String screenOff = "MainScreen is ON at : " + mydate;
//            generateNoteOnSD(getApplicationContext(), screenOff);
//        } else {
//
//            // this is when onResume() is called when the screen state has not changed
//            System.out.println(" this is when onResume() is called when the screen state has not changed ");
//        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
//        if (UpdateService.ScreenReceiver.screenOff) {
//            // this is the case when onPause() is called by the system due to a screen state change
//            System.out.println("SCREEN TURNED OFF");
//            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//            String screenOff = "MainScreen is OFF at : " + mydate;
//            generateNoteOnSD(getApplicationContext(), screenOff);
//
//        } else {
//            // this is when onPause() is called when the screen state has not changed
//            System.out.println("this is when onPause() is called when the screen state has not changed ");
//
//        }
        super.onPause();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} starts receiving callbacks when
     * activities are detected.
     */
    public void requestActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     */
    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

            // Update the UI. Requesting activity updates enables the Remove Activity Updates
            // button, and removing activity updates enables the Add Activity Updates button.
            setButtonsEnabledState();

            Toast.makeText(
                    this,
                    getString(requestingUpdates ? R.string.activity_updates_added :
                            R.string.activity_updates_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Ensures that only one button is enabled at any time. The Request Activity Updates button is
     * enabled if the user hasn't yet requested activity updates. The Remove Activity Updates button
     * is enabled if the user has requested activity updates.
     */
    private void setButtonsEnabledState() {
        if (getUpdatesRequestedState()) {
            mRequestActivityUpdatesButton.setEnabled(false);
            mRemoveActivityUpdatesButton.setEnabled(true);
        } else {
            mRequestActivityUpdatesButton.setEnabled(true);
            mRemoveActivityUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Retrieves a SharedPreference object used to store or read values in this app. If a
     * preferences file passed as the first argument to {@link #getSharedPreferences}
     * does not exist, it is created when {@link SharedPreferences.Editor} is used to commit
     * data.
     */
    private SharedPreferences getSharedPreferencesInstance() {
        return getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .commit();
    }

    /**
     * Stores the list of detected activities in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(Constants.DETECTED_ACTIVITIES, mDetectedActivities);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Processes the list of freshly detected activities. Asks the adapter to update its list of
     * DetectedActivities with new {@code DetectedActivity} objects reflecting the latest detected
     * activities.
     */
    protected void updateDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities) {
        mAdapter.updateActivities(detectedActivities);
    }

    ///////////////////////////////////////////////////////
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
    }

    //network availability for location trace
    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            updateDetectedActivitiesList(updatedActivities);

            setDetectedActivitiesData(updatedActivities);

            float[] accData = intent.getFloatArrayExtra(Constants.EXTRA_ACC_DATA);
            float[] gyrData = intent.getFloatArrayExtra(Constants.EXTRA_GYR_DATA);
            float[] megData = intent.getFloatArrayExtra(Constants.EXTRA_MEG_DATA);
            exeSendData(accData, gyrData, megData);
        }
    }

    private void exeSendData(float[] accData, float[] gyrData, float[] megData) {
        new SendActivityDataTask(sendActivityDataInterface, accData, gyrData, megData).execute(
                Constants.SERVER_URL + Constants.API_ENDPOINT_FIND_MATCH_STATE,
                prefs.getValue(Constants.PREF_AUTH_TOKEN, "")
        );
    }


    private void setupUI() {
        if (userType == UserType.CLIENT) {
            layoutClientView.setVisibility(View.VISIBLE);
            layoutDoctorView.setVisibility(View.GONE);
        } else if (userType == UserType.DOCTOR) {
            layoutClientView.setVisibility(View.GONE);
            layoutDoctorView.setVisibility(View.VISIBLE);
        }
    }

    private void setDetectedActivitiesData(ArrayList<DetectedActivity> updatedActivities) {
        HashMap<Integer, Integer> detectedActivitiesMap = new HashMap<>();
        for (DetectedActivity activity : updatedActivities) {
            detectedActivitiesMap.put(activity.getType(), activity.getConfidence());
        }
        // Every time we detect new activities, we want to reset the confidence level of ALL
        // activities that we monitor. Since we cannot directly change the confidence
        // of a DetectedActivity, we use a temporary list of DetectedActivity objects. If an
        // activity was freshly detected, we use its confidence level. Otherwise, we set the
        // confidence level to zero.
        mDetectedActivitiesData = new ArrayList<DetectedActivity>();
        for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
            int confidence = detectedActivitiesMap.containsKey(Constants.MONITORED_ACTIVITIES[i]) ?
                    detectedActivitiesMap.get(Constants.MONITORED_ACTIVITIES[i]) : 0;
            mDetectedActivitiesData.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], confidence));
        }
    }
}

