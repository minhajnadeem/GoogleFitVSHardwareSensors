package com.example.abdul.googlefitvshardwaresensors.tasks;

import android.os.AsyncTask;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.interfaces.GetFeedbackInterface;
import com.example.abdul.googlefitvshardwaresensors.interfaces.SendActivityDataInterface;
import com.example.abdul.googlefitvshardwaresensors.utils.Parser;
import com.example.abdul.googlefitvshardwaresensors.utils.Webclient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Minhaj on 23/12/2018.
 */
public class GetFeedbackTask extends AsyncTask<String,Void,String> {

    private String serverResponse = null, error = null,feedback = "",doctorName = "";
    private GetFeedbackInterface getFeedbackInterface;
    private Gson gson;

    public GetFeedbackTask(GetFeedbackInterface getFeedbackInterface) {
        this.getFeedbackInterface = getFeedbackInterface;
    }

    @Override
    protected String doInBackground(String... strings) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate(Constants.API_PARAM_PATIENT_ID,Integer.parseInt(strings[1]));

            serverResponse = Webclient.postRequest(strings[0],jsonObject);

            if (serverResponse != null){
                if (serverResponse.contains("error_code")){
                    error = Parser.parseError(serverResponse);
                }else {
                    jsonObject = new JSONObject(serverResponse);
                    feedback = jsonObject.getString("feedback");
                    doctorName = jsonObject.getString("doctor_name");
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        getFeedbackInterface.onGetFeedbackResult(error,feedback,doctorName);
    }
}
