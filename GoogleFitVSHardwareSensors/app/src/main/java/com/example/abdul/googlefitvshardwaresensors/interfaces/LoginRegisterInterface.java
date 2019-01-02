package com.example.abdul.googlefitvshardwaresensors.interfaces;

import com.example.abdul.googlefitvshardwaresensors.models.UserModel;

/**
 * Created by Minhaj on 23/12/2018.
 */
public interface LoginRegisterInterface {

    void onLoginResult(String error, UserModel userModel);
}
