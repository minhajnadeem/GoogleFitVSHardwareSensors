package com.example.abdul.googlefitvshardwaresensors.tasks;

import android.os.AsyncTask;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.interfaces.LogoutInterface;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.example.abdul.googlefitvshardwaresensors.utils.Parser;
import com.example.abdul.googlefitvshardwaresensors.utils.Webclient;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Minhaj on 01/01/2019.
 */
public class LogoutTask extends AsyncTask<String, Void, String> {

    private String serverResponse = null, error = null;
    private boolean success;
    private LogoutInterface logoutInterface;

    public LogoutTask(LogoutInterface logoutInterface) {
        this.logoutInterface = logoutInterface;
    }

    @Override
    protected String doInBackground(String... strings) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate(Constants.API_PARAM_AUTH_TOKEN, strings[1]);

            serverResponse = Webclient.postRequest(strings[0], jsonObject);

            if (serverResponse != null) {
                if (serverResponse.contains("rest_error")) {
                    error = Parser.parseError(serverResponse);
                } else {
                    jsonObject = new JSONObject(serverResponse);
                    success = jsonObject.getBoolean("success");
                    if (!success)
                        error = jsonObject.getString("message");
                }
            } else {
                error = Constants.ERROR;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            error = Constants.ERROR;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        logoutInterface.onLogoutResult(error, success);
    }
}
