package com.swagger.app;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;


public class SplashActivity extends AppCompatActivity {
    int SPLASH_TIME_OUT = 2000;
    ProgressBar pBar;
    SharedPreferenceClass sharedPreferenceClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);
      /*  Intent intent = new Intent(SplashActivity.this, ServiceSelectionActivity.class);
        startActivity(intent);
        finish();*/
        pBar = findViewById(R.id.pBar);
        sharedPreferenceClass = new SharedPreferenceClass(this);

        boolean hasInternet = Common.checkNetworkConnection(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {
            // Showing splash screen with a timer.
            @Override
            public void run() {
                try {
                    /*SharedPreferences sp = getSharedPreferences("userCred", MODE_PRIVATE);
                    boolean isReg = sp.getBoolean("isReg", false);*/
                    boolean isReg = sharedPreferenceClass.getValue_boolean(StaticVariables.IS_REGISTERED);
                    if (isReg) {
                        if (sharedPreferenceClass.getValue_string(StaticVariables.USER_TYPE).equals("P")) {
                            if (sharedPreferenceClass.getValue_boolean(StaticVariables.IS_VENDER_REGISTERED)) {

                                Intent intent = new Intent(SplashActivity.this, VenderProfileActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                Intent intent = new Intent(SplashActivity.this, ServiceSelectionActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else if (sharedPreferenceClass.getValue_string(StaticVariables.USER_TYPE).equals("U")) {
                            Intent intent = new Intent(SplashActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }, SPLASH_TIME_OUT);


    }

}
