package com.example.abdul.googlefitvshardwaresensors.utils;

import android.util.Log;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;


public final class Parser {

    private static final String TAG = "parser";
    private Gson gson;

    /**
     *
     * error codes:
     * 1 = undefine
     * 2 = deAuthenticated
     * 3 = duplicate
     * 4 = does not exists
     *
     */
    public static String parseError(String serverResponse){
        String errorMessage = Constants.ERROR;

        try {
            JSONObject jsonObject = new JSONObject(serverResponse);
            int errorCode = jsonObject.getInt("rest_error_code");
            errorMessage = errorCode + "";
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseError: "+e.getMessage() );
        }

        return errorMessage;
    }

}
