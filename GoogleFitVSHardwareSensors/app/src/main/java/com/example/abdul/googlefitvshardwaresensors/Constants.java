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

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

/**
 * Constants used in this sample.
 */
public final class Constants {

    public static final String PACKAGE_NAME = "com.google.android.gms.location.activityrecognition";
    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";
    public static final String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME +".ACTIVITY_UPDATES_REQUESTED";
    public static final String DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";
    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 100;
    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    protected static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    private Constants() {
    }

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    //server urls
    public static final String LOCAL_SERVER = "http://192.168.10.15:8000/client/";
    public static final String SERVER_URL = LOCAL_SERVER;

    //APIs endpoint
    public static final String API_ENDPOINT_REGISTER = "register";
    public static final String API_ENDPOINT_LOGIN = "login";
    public static final String API_ENDPOINT_LOGOUT = "logout";
    public static final String API_ENDPOINT_GET_USERS = "get_users";
    public static final String API_ENDPOINT_FIND_MATCH_STATE = "find_match_state";
    public static final String API_ENDPOINT_GET_FEEDBACK = "get_feedback";
    public static final String API_ENDPOINT_GET_USER_ACTIVITY = "get_user_last_state";
    public static final String API_ENDPOINT_SEND_FEEDBACK = "give_feedback";

    //int
        //user type
    public static final int USER_TYPE_DOCTOR = 2;  //user type doctor
    public static final int USER_TYPE_CLIENT = 1;  //user type client

    //double
    public static final double API_VERSION = 1.0;

    //string
        //prefs
    public static final String PREF_USER_TYPE = "pref_user_type";
    public static final String PREF_USERNAME = "pref_username";
    public static final String PREF_AUTH_TOKEN = "pref_auth_token";
    public static final String PREF_USER_ID = "pref_user_id";
        //extra
    public static final String EXTRA_USER = "extra_user";
    public static final String EXTRA_ACC_DATA = "extra_acc_data";
    public static final String EXTRA_GYR_DATA = "extra_gyr_data";
    public static final String EXTRA_MEG_DATA = "extra_meg_data";
        //params
    public static final String API_PARAM_USERNAME = "username";
    public static final String API_PARAM_PASSWORD = "password";
    public static final String API_PARAM_USER_TYPE = "type";
    public static final String API_PARAM_AUTH_TOKEN = "auth_token";
    public static final String API_PARAM_ID = "id";
    public static final String API_PARAM_PATIENT_ID = "patient_id";
    public static final String API_PARAM_DOCTOR_ID = "doctor_id";
    public static final String API_PARAM_FEEDBACK_MESSAGE = "feedback_message";
    public static final String API_PARAM_AX = "ax";
    public static final String API_PARAM_AY = "ay";
    public static final String API_PARAM_AZ = "az";
    public static final String API_PARAM_GX = "gx";
    public static final String API_PARAM_GY = "gy";
    public static final String API_PARAM_GZ = "gz";
    public static final String API_PARAM_MX = "mx";
    public static final String API_PARAM_MY = "my";
    public static final String API_PARAM_MZ = "mz";
        //error
    public static final String ERROR = "something went wrong. Try again!";
}
