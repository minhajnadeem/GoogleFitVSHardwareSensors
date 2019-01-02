package com.example.abdul.googlefitvshardwaresensors.tasks;

import android.os.AsyncTask;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.interfaces.LoginRegisterInterface;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.example.abdul.googlefitvshardwaresensors.utils.Parser;
import com.example.abdul.googlefitvshardwaresensors.utils.Webclient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Minhaj on 23/12/2018.
 */
public class LoginRegisterTask extends AsyncTask<String,Void,UserModel> {

    private String serverResponse = null, error = null;
    private boolean success;
    private UserModel userModel;
    private LoginRegisterInterface loginRegisterInterface;
    private Gson gson;

    public LoginRegisterTask(LoginRegisterInterface loginRegisterInterface) {
        this.loginRegisterInterface = loginRegisterInterface;
        gson = new Gson();
    }

    @Override
    protected UserModel doInBackground(String... strings) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constants.API_PARAM_USERNAME,strings[1]);
            jsonObject.put(Constants.API_PARAM_PASSWORD,strings[2]);
            jsonObject.put(Constants.API_PARAM_USER_TYPE,strings[3]);

            serverResponse = Webclient.postRequest(strings[0],jsonObject);

            if (serverResponse != null){
                if (serverResponse.contains("rest_error")){
                    error = Parser.parseError(serverResponse);
                }else {
                    jsonObject = new JSONObject(serverResponse);
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        userModel = gson.fromJson(serverResponse, UserModel.class);
                    }else {
                        error = jsonObject.getString("message");
                    }
                }
            }else {
                error = Constants.ERROR;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userModel;
    }

    @Override
    protected void onPostExecute(UserModel userModel) {
        super.onPostExecute(userModel);
        loginRegisterInterface.onLoginResult(error,userModel);
    }
}
