package com.example.abdul.googlefitvshardwaresensors.tasks;

import android.os.AsyncTask;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.interfaces.FeedbackInterface;
import com.example.abdul.googlefitvshardwaresensors.utils.Parser;
import com.example.abdul.googlefitvshardwaresensors.utils.Webclient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Minhaj on 23/12/2018.
 */
public class FeedbackTask extends AsyncTask<String,Void,Void> {

    private String serverResponse = null, error = null,message = "";
    private boolean success;
    private FeedbackInterface feedbackInterface;
    private Gson gson;

    public FeedbackTask(FeedbackInterface feedbackInterface) {
        this.feedbackInterface = feedbackInterface;
    }

    @Override
    protected Void doInBackground(String... strings) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate(Constants.API_PARAM_AUTH_TOKEN,strings[1]);
            jsonObject.accumulate(Constants.API_PARAM_PATIENT_ID,Integer.parseInt(strings[2]));
            jsonObject.accumulate(Constants.API_PARAM_DOCTOR_ID,Integer.parseInt(strings[3]));
            jsonObject.accumulate(Constants.API_PARAM_FEEDBACK_MESSAGE,strings[4]);

            serverResponse = Webclient.postRequest(strings[0],jsonObject);

            if (serverResponse != null){
                if (serverResponse.contains("error_code")){
                    error = Parser.parseError(serverResponse);
                }else {
                    jsonObject = new JSONObject(serverResponse);
                    success = jsonObject.getBoolean("success");
                    if (jsonObject.has("message"))
                        message = jsonObject.getString("message");
                }
            }else {
                error = Constants.ERROR;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        feedbackInterface.onFeedbackResult(error,success,message);
    }
}
