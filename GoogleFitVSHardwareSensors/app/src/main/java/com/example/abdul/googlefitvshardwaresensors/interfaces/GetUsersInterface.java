package com.example.abdul.googlefitvshardwaresensors.interfaces;

import com.example.abdul.googlefitvshardwaresensors.models.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minhaj on 23/12/2018.
 */
public interface GetUsersInterface {
    void onGetUsersResult(String error, ArrayList<UserModel> userModelList);
}
