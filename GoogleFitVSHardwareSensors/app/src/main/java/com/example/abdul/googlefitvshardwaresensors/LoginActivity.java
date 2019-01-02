package com.example.abdul.googlefitvshardwaresensors;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdul.googlefitvshardwaresensors.interfaces.LoginRegisterInterface;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.example.abdul.googlefitvshardwaresensors.tasks.LoginRegisterTask;
import com.example.abdul.googlefitvshardwaresensors.utils.Prefs;

public class LoginActivity extends AppCompatActivity {

    enum RequestType {
        LOGIN,
        REGISTER
    }

    public enum UserType {
        DOCTOR(Constants.USER_TYPE_DOCTOR),
        CLIENT(Constants.USER_TYPE_CLIENT);

        private final int userType;

        UserType(int type) {
            this.userType = type;
        }

        public int getUserType() {
            return this.userType;
        }
    }

    //varibles
    private RequestType requestType;
    private UserType userType;
    private Prefs prefs;
    private LoginRegisterInterface loginRegisterInterface;

    //views
    private TextInputLayout etUsernameLayout, etPasswordLayout;
    private TextInputEditText etUsernmae, etPassword;
    private Button loginBtn;
    private ProgressBar progressBar;
    private Spinner spinnerUserType;
    private TextView tvRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        if (!prefs.getValue(Constants.PREF_AUTH_TOKEN,"").equals("")){
            launchHomeScreen();
        }
    }

    private void init() {

        //getSupportActionBar().setTitle("Login");

        prefs = new Prefs(this);

        requestType = RequestType.LOGIN;

        userType = UserType.CLIENT;

        etUsernameLayout = findViewById(R.id.et_username_layout);
        etPasswordLayout = findViewById(R.id.et_password_layout);
        etUsernmae = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        loginBtn = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        tvRegister = findViewById(R.id.tvRegister);

        spinnerUserType = findViewById(R.id.spinnerUserType);
        spinnerUserType.setVisibility(View.GONE);

        //set spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerUserType.setAdapter(adapter);

        setupListeners();
        setupUI();
    }

    /**
     * implementation of listeners
     */
    private void setupListeners() {

        //login user
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    userType = UserType.CLIENT;
                } else if (position == 1) {
                    userType = UserType.DOCTOR;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //login delegate
        loginRegisterInterface = new LoginRegisterInterface() {
            @Override
            public void onLoginResult(String error, UserModel userModel) {
                if (error == null){
                    if (userModel != null){
                        prefs.setValue(Constants.PREF_USERNAME,userModel.getUsername());
                        prefs.setValue(Constants.PREF_AUTH_TOKEN,userModel.getAuth_token());
                        prefs.setInt(Constants.PREF_USER_TYPE,userModel.getType());
                        prefs.setInt(Constants.PREF_USER_ID,userModel.getId());
                        launchHomeScreen();
                    }else {
                        Toast.makeText(LoginActivity.this, Constants.ERROR, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void setupUI() {

        if (requestType == RequestType.REGISTER) {
            getSupportActionBar().setTitle("Register");
            spinnerUserType.setVisibility(View.VISIBLE);
            tvRegister.setText("Already a user? Login");
            tvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestType = RequestType.LOGIN;
                    setupUI();
                }
            });
        } else if (requestType == RequestType.LOGIN) {
            getSupportActionBar().setTitle("Login");
            spinnerUserType.setVisibility(View.GONE);
            tvRegister.setText("Not a user? Register");
            tvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestType = RequestType.REGISTER;
                    setupUI();
                }
            });
        }
    }

    /**
     * make API call after validate
     */
    private void login() {
        //todo:integrate login/register API
        if (!validate()) {
            Toast.makeText(this, "please provide username and password", Toast.LENGTH_SHORT).show();
        } else {
            String apiEndpoint = "";
            if (requestType == RequestType.LOGIN)
                apiEndpoint = Constants.API_ENDPOINT_LOGIN;
            else if (requestType == RequestType.REGISTER)
                apiEndpoint = Constants.API_ENDPOINT_REGISTER;

            new LoginRegisterTask(loginRegisterInterface).execute(
                    Constants.SERVER_URL + apiEndpoint,
                    etUsernmae.getText().toString().trim(),
                    etPassword.getText().toString(),
                    userType.getUserType() + ""
                    );
        }
        /*Toast.makeText(this, requestType + " - " + userType.getUserType(), Toast.LENGTH_SHORT).show();
        launchHomeScreen();
        prefs.setInt(Constants.PREF_USER_TYPE, userType.getUserType());*/
    }

    /**
     * validate fields
     * fields must not empty
     */
    private boolean validate() {
        View focusView = null;
        boolean valid = true;
        String errorMsg = "";

        if (etUsernmae.getText().toString().trim().isEmpty()) {
            focusView = etUsernmae;
            valid = false;
            errorMsg = getString(R.string.error_enter_username);
        } else if (etPassword.getText().toString().trim().isEmpty()) {
            focusView = etPassword;
            valid = false;
            errorMsg = getString(R.string.error_enter_password);
        }

        if (!valid) {
            ((EditText) focusView).setError(errorMsg, null);
            focusView.requestFocus();
        }
        return valid;
    }

    /**
     * launch home screen and clear activities stack
     */
    private void launchHomeScreen() {
        Intent launch = new Intent(this, MainActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launch);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
