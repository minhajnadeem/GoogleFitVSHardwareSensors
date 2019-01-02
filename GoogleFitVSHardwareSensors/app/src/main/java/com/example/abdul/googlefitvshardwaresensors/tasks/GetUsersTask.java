package com.example.abdul.googlefitvshardwaresensors.tasks;

import android.os.AsyncTask;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.interfaces.GetFeedbackInterface;
import com.example.abdul.googlefitvshardwaresensors.interfaces.GetUsersInterface;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.example.abdul.googlefitvshardwaresensors.utils.Parser;
import com.example.abdul.googlefitvshardwaresensors.utils.Webclient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Minhaj on 23/12/2018.
 */
public class GetUsersTask extends AsyncTask<String, Void, List<UserModel>> {

    private String serverResponse = null, error = null;
    private ArrayList<UserModel> userModelList;
    private GetUsersInterface getUsersInterface;
    private Gson gson;

    public GetUsersTask(GetUsersInterface getUsersInterface) {
        this.getUsersInterface = getUsersInterface;
        gson = new Gson();
    }

    @Override
    protected List<UserModel> doInBackground(String... strings) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate(Constants.API_PARAM_AUTH_TOKEN, strings[1]);

            serverResponse = Webclient.postRequest(strings[0], jsonObject);

            if (serverResponse != null) {
                if (serverResponse.contains("error_code")) {
                    error = Parser.parseError(serverResponse);
                } else {
                    Type type = new TypeToken<Collection<UserModel>>() {}.getType();
                    userModelList = gson.fromJson(serverResponse, type);
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
    protected void onPostExecute(List<UserModel> userModels) {
        super.onPostExecute(userModels);
        getUsersInterface.onGetUsersResult(error, userModelList);
    }
}
