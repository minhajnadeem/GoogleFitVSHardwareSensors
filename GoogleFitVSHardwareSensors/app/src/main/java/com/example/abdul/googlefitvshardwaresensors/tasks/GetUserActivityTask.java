package com.example.abdul.googlefitvshardwaresensors.tasks;

import android.os.AsyncTask;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.interfaces.GetUserActivityInterface;
import com.example.abdul.googlefitvshardwaresensors.utils.Parser;
import com.example.abdul.googlefitvshardwaresensors.utils.Webclient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Minhaj on 02/01/2019.
 */
public class GetUserActivityTask extends AsyncTask<String, Void, String> {

    private String serverResponse = null, error = null, userActivity = "";
    private GetUserActivityInterface getUserActivityInterface;
    private Gson gson;

    public GetUserActivityTask(GetUserActivityInterface getUserActivityInterface) {
        this.getUserActivityInterface = getUserActivityInterface;
    }

    @Override
    protected String doInBackground(String... strings) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate(Constants.API_PARAM_AUTH_TOKEN, strings[1]);
            jsonObject.accumulate(Constants.API_PARAM_PATIENT_ID, Integer.parseInt(strings[2]));

            serverResponse = Webclient.postRequest(strings[0], jsonObject);

            if (serverResponse != null) {
                if (serverResponse.contains("error_code")) {
                    error = Parser.parseError(serverResponse);
                } else {
                    jsonObject = new JSONObject(serverResponse);
                    userActivity = jsonObject.getString("state");
                }
            } else {
                error = Constants.ERROR;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        getUserActivityInterface.onGetUserActivityResult(error, userActivity);
    }
}
