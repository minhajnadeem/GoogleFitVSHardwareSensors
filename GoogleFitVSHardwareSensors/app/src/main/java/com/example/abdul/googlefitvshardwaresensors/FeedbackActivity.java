package com.example.abdul.googlefitvshardwaresensors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdul.googlefitvshardwaresensors.interfaces.FeedbackInterface;
import com.example.abdul.googlefitvshardwaresensors.interfaces.GetUserActivityInterface;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;
import com.example.abdul.googlefitvshardwaresensors.tasks.FeedbackTask;
import com.example.abdul.googlefitvshardwaresensors.tasks.GetUserActivityTask;
import com.example.abdul.googlefitvshardwaresensors.utils.Prefs;

public class FeedbackActivity extends AppCompatActivity {

    private Prefs prefs;

    private TextView tvUserName,tvUserActivity;
    private EditText etFeedback;
    private Button btnSendFeedback;

    private UserModel userModel;

    private GetUserActivityInterface getUserActivityInterface;
    private FeedbackInterface feedbackInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        prefs = new Prefs(this);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserActivity = findViewById(R.id.tvUserActivity);

        etFeedback = findViewById(R.id.etFeedback);

        btnSendFeedback = findViewById(R.id.btnSendFeedback);

        userModel = getIntent().getParcelableExtra(Constants.EXTRA_USER);

        tvUserName.setText(userModel.getUsername());

        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FeedbackTask(feedbackInterface).execute(
                        Constants.SERVER_URL + Constants.API_ENDPOINT_SEND_FEEDBACK,
                        prefs.getValue(Constants.PREF_AUTH_TOKEN,""),
                        userModel.getId() + "",
                        prefs.getInt(Constants.PREF_USER_ID,-1) + "",
                        etFeedback.getText().toString().trim()

                );
            }
        });

        getUserActivityInterface = new GetUserActivityInterface() {
            @Override
            public void onGetUserActivityResult(String error, String userActivity) {
                if (error == null){
                    tvUserActivity.setText(userActivity);
                }else {
                    Toast.makeText(FeedbackActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        };
        feedbackInterface = new FeedbackInterface() {
            @Override
            public void onFeedbackResult(String error, boolean success, String message) {
                finish();
            }
        };

        new GetUserActivityTask(getUserActivityInterface).execute(
                Constants.SERVER_URL + Constants.API_ENDPOINT_GET_USER_ACTIVITY,
                prefs.getValue(Constants.PREF_AUTH_TOKEN,""),
                userModel.getId() + ""
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        userModel = intent.getParcelableExtra(Constants.EXTRA_USER);
        tvUserName.setText(userModel.getUsername());
    }
}
