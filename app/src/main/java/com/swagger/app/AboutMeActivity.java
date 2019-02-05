package com.swagger.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

public class AboutMeActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText aboutmeTxt;
    Button submitBtn;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initWidgets();
    }

    private void initWidgets() {
        aboutmeTxt = findViewById(R.id.aboutmeTxt);
        submitBtn = findViewById(R.id.submitBtn);
        pBar = findViewById(R.id.pBar);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aboutmeTxt.getText().toString().trim().length() != 0) {
                    if (Common.checkNetworkConnection(AboutMeActivity.this)) {
                        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(AboutMeActivity.this);
                        sendPostToServer(aboutmeTxt.getText().toString().trim(), sharedPreferenceClass.getValue_string(StaticVariables.USER_ID),sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
                    } else {
                        Toast.makeText(AboutMeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AboutMeActivity.this, "Write something to upload", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void sendPostToServer(final String post, String userId, String access_token) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.addHeader("Authorization", "bearer "+access_token);

        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("userId", userId);
            jsonParams.put("about", post);
            StringEntity entity = new StringEntity(jsonParams.toString());

            client.post(this, Common.uploadAbutMeUrl, entity, "application/json", new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    pBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    pBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                    try {

                        Toast.makeText(AboutMeActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                    pBar.setVisibility(View.GONE);
                    Toast.makeText(AboutMeActivity.this, "Your about me status updated successfully", Toast.LENGTH_SHORT).show();
                    aboutmeTxt.getText().clear();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
