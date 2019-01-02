package com.example.abdul.googlefitvshardwaresensors.tasks;

import android.os.AsyncTask;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.interfaces.LoginRegisterInterface;
import com.example.abdul.googlefitvshardwaresensors.interfaces.SendActivityDataInterface;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.example.abdul.googlefitvshardwaresensors.utils.Parser;
import com.example.abdul.googlefitvshardwaresensors.utils.Webclient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Minhaj on 23/12/2018.
 */
public class SendActivityDataTask extends AsyncTask<String, Void, Void> {

    private String serverResponse = null, error = null;
    private SendActivityDataInterface sendActivityDataInterface;
    private Gson gson;
    float[] accData;
    float[] gyrData;
    float[] megData;

    public SendActivityDataTask(SendActivityDataInterface sendActivityDataInterface, float[] accData, float[] gyrData, float[] megData) {
        this.sendActivityDataInterface = sendActivityDataInterface;
        this.accData = accData;
        this.gyrData = gyrData;
        this.megData = megData;
    }

    @Override
    protected Void doInBackground(String... strings) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate(Constants.API_PARAM_AUTH_TOKEN, strings[1]);
            jsonObject.accumulate(Constants.API_PARAM_AX, accData[0]);
            jsonObject.accumulate(Constants.API_PARAM_AY, accData[1]);
            jsonObject.accumulate(Constants.API_PARAM_AZ, accData[2]);

            jsonObject.accumulate(Constants.API_PARAM_GX, gyrData[0]);
            jsonObject.accumulate(Constants.API_PARAM_GY, gyrData[1]);
            jsonObject.accumulate(Constants.API_PARAM_GZ, gyrData[2]);

            jsonObject.accumulate(Constants.API_PARAM_MX, megData[0]);
            jsonObject.accumulate(Constants.API_PARAM_MY, megData[1]);
            jsonObject.accumulate(Constants.API_PARAM_MZ, megData[2]);

            serverResponse = Webclient.postRequest(strings[0], jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
