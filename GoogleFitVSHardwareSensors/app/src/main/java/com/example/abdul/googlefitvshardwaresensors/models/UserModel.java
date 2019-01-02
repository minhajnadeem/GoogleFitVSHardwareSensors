package com.example.abdul.googlefitvshardwaresensors.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Minhaj on 20/12/2018.
 */
public class UserModel implements Parcelable {
    private String username,auth_token;
    private int id,type;

    public UserModel(String username, String auth_token, int type, int id) {
        this.username = username;
        this.auth_token = auth_token;
        this.type = type;
        this.id = id;
    }

    protected UserModel(Parcel in) {
        username = in.readString();
        auth_token = in.readString();
        id = in.readInt();
        type = in.readInt();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(auth_token);
        dest.writeInt(id);
        dest.writeInt(type);
    }
}
