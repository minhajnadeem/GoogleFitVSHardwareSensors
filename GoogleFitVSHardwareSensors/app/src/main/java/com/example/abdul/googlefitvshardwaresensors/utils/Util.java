package com.example.abdul.googlefitvshardwaresensors.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.abdul.googlefitvshardwaresensors.LoginActivity;

/**
 * Created by Minhaj on 01/01/2019.
 */
public class Util {

    public static void doAfterLogout(Activity activity){
        Intent intent = new Intent(activity,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
