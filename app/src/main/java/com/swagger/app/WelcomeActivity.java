package com.swagger.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout welcomeLayout, postWelcomeLayout, otpLayout, registrationLayout;
    ProgressBar pBar;
    EditText firstNameTxt, lastNameTxt, mobileNoTxt, passwordTxt, otpTxt, phoneNoTxt, loginPasswordTxt, confirmPasswordTxt;
    TextView appNameTxt;
    Button registerBtn, signUpBtn, loginBtn, userRegBtn, partnerRegBtn, postWelcomeloginBtn, verifyBtn;
    SharedPreferenceClass sharedPreferenceClass;
    boolean isPartner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initWidgets();
    }

    private void initWidgets() {
        sharedPreferenceClass = new SharedPreferenceClass(this);
        boolean isReg = getIntent().getBooleanExtra("isReg", false);
        welcomeLayout = findViewById(R.id.welcomeLayout);
        postWelcomeLayout = findViewById(R.id.postWelcomeLayout);
        otpLayout = findViewById(R.id.otpLayout);
        registrationLayout = findViewById(R.id.registrationLayout);
        firstNameTxt = findViewById(R.id.firstNameTxt);
        lastNameTxt = findViewById(R.id.lastNameTxt);
        mobileNoTxt = findViewById(R.id.mobileNoTxt);
        passwordTxt = findViewById(R.id.passwordTxt);
        otpTxt = findViewById(R.id.otpTxt);
        phoneNoTxt = findViewById(R.id.phoneNoTxt);
        appNameTxt = findViewById(R.id.appNameTxt);
        registerBtn = findViewById(R.id.registerBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.loginBtn);
        userRegBtn = findViewById(R.id.userRegBtn);
        partnerRegBtn = findViewById(R.id.partnerRegBtn);
        postWelcomeloginBtn = findViewById(R.id.postWelcomeloginBtn);
        pBar = findViewById(R.id.pBar);
        loginPasswordTxt = findViewById(R.id.loginPasswordTxt);
        verifyBtn = findViewById(R.id.verifyBtn);
        confirmPasswordTxt = findViewById(R.id.confirmPasswordTxt);

        welcomeLayout.setVisibility(View.VISIBLE);


        verifyBtn.setOnClickListener(view -> {
            // SharedPreferences userPref = getSharedPreferences("userCred", MODE_PRIVATE);
            if (otpTxt.getText().toString().trim().length() != 0) {
                if (Common.checkNetworkConnection(WelcomeActivity.this)) {
                    //  validateOtp(userPref.getString("firstName", ""), userPref.getString("lastName", ""), userPref.getString("mobile", ""), userPref.getString("password", ""), otpTxt.getText().toString().trim());

                    validateOtp(sharedPreferenceClass.getValue_string(StaticVariables.FIRST_NAME), sharedPreferenceClass.getValue_string(StaticVariables.LAST_NAME), sharedPreferenceClass.getValue_string(StaticVariables.MOBILE_NUMBER), sharedPreferenceClass.getValue_string(StaticVariables.PASSWORD), otpTxt.getText().toString().trim(), isPartner);

                } else {
                    Toast.makeText(WelcomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(WelcomeActivity.this, "Otp cannot be empty", Toast.LENGTH_SHORT).show();
        });
        registerBtn.setOnClickListener(view -> {
            if (firstNameTxt.getText().toString().trim().length() != 0 || lastNameTxt.getText().toString().length() != 0 || passwordTxt.getText().toString().trim().length() != 0 || mobileNoTxt.getText().toString().trim().length() != 0) {
                if (confirmPasswordTxt.getText().toString().trim().equals(passwordTxt.getText().toString().trim())) {
                    if (Common.checkNetworkConnection(this)) {
                        registerUser(firstNameTxt.getText().toString().trim(), lastNameTxt.getText().toString().trim(), mobileNoTxt.getText().toString().trim(), passwordTxt.getText().toString().trim());
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WelcomeActivity.this, "Both password & confirm password should be identical", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(WelcomeActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
        });

        loginBtn.setOnClickListener(view -> {
            if (phoneNoTxt.getText().toString().trim().length() != 0 || loginPasswordTxt.getText().toString().trim().length() != 0) {
                if (Common.checkNetworkConnection(this)) {
                    doUserLogin(phoneNoTxt.getText().toString().trim(), loginPasswordTxt.getText().toString().trim());
                } else {
                    Toast.makeText(WelcomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(WelcomeActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
        });
        signUpBtn.setOnClickListener(view -> {
            welcomeLayout.setVisibility(View.GONE);
            postWelcomeLayout.setVisibility(View.VISIBLE);
        });

        userRegBtn.setOnClickListener(view -> {
            welcomeLayout.setVisibility(View.GONE);
            postWelcomeLayout.setVisibility(View.GONE);
            registrationLayout.setVisibility(View.VISIBLE);
            isPartner = false;
        });

        partnerRegBtn.setOnClickListener(view -> {
            welcomeLayout.setVisibility(View.GONE);
            postWelcomeLayout.setVisibility(View.GONE);
            registrationLayout.setVisibility(View.VISIBLE);
            isPartner = true;
        });

        postWelcomeloginBtn.setOnClickListener(view -> {
            postWelcomeLayout.setVisibility(View.GONE);
            welcomeLayout.setVisibility(View.VISIBLE);
        });

    }

    private void registerUser(String firstName, String lastName, String mobileNo, String password) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("password", password);
            jsonParams.put("confirmPassword", password);
            jsonParams.put("firstName", firstName);
            jsonParams.put("lastName", lastName);
            jsonParams.put("mobileNo", mobileNo);
            StringEntity entity = new StringEntity(jsonParams.toString());

            client.post(this, Common.userRegUrl, entity, "application/json", new TextHttpResponseHandler() {

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
                        JSONObject jsonObject = new JSONObject(response);
                        String error = jsonObject.optString("error");
                        if (!error.isEmpty() && error != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                            builder.setCancelable(true).setTitle(error).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    registrationLayout.setVisibility(View.GONE);
                                    welcomeLayout.setVisibility(View.VISIBLE);
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                    registrationLayout.setVisibility(View.GONE);
                    otpLayout.setVisibility(View.VISIBLE);

                    sharedPreferenceClass.setValue_string(StaticVariables.FIRST_NAME, firstName);
                    sharedPreferenceClass.setValue_string(StaticVariables.LAST_NAME, lastName);
                    sharedPreferenceClass.setValue_string(StaticVariables.PASSWORD, password);
                    sharedPreferenceClass.setValue_string(StaticVariables.MOBILE_NUMBER, mobileNo);


                   /* SharedPreferences userPref = getSharedPreferences("userCred", MODE_PRIVATE);
                    userPref.edit().putString("firstName", firstName).apply();
                    userPref.edit().putString("lastName", lastName).apply();
                    userPref.edit().putString("mobile", mobileNo).apply();
                    userPref.edit().putString("password", password).apply();*/
                    firstNameTxt.getText().clear();
                    lastNameTxt.getText().clear();
                    mobileNoTxt.getText().clear();
                    passwordTxt.getText().clear();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void validateOtp(String firstName, String lastName, String mobileNo, String password, String otp, boolean isPartner) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("password", password);
            jsonParams.put("confirmPassword", password);
            jsonParams.put("firstName", firstName);
            jsonParams.put("lastName", lastName);
            jsonParams.put("otp", otp);
            jsonParams.put("mobileNo", mobileNo);
            StringEntity entity = new StringEntity(jsonParams.toString());
            String validateUrl = "";
            if (isPartner) {
                validateUrl = Common.validateOtpPartnerUrl;
            } else
                validateUrl = Common.validateOtpUserUrl;

            client.post(this, validateUrl, entity, "application/json", new TextHttpResponseHandler() {

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
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Toast.makeText(WelcomeActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                    try {
                        sharedPreferenceClass.setValue_boolean(StaticVariables.IS_REGISTERED, true);
                        /*SharedPreferences userPref = getSharedPreferences("userCred", MODE_PRIVATE);
                        userPref.edit().putBoolean("isReg", true).apply();*/
                        otpLayout.setVisibility(View.GONE);
                        welcomeLayout.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doUserLogin(String userName, String password) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        RequestParams params = new RequestParams();
        try {
            params.put("grant_type", "password");
            params.put("username", userName);
            params.put("password", password);

            StringEntity entity = new StringEntity(params.toString());

            client.post(this, Common.loginUrl, entity, "application/x-www-form-urlencoded", new TextHttpResponseHandler() {

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
                    Toast.makeText(WelcomeActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String error = jsonObject.optString("error_description");

                        if (!error.isEmpty() && error != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                            builder.setCancelable(true).setTitle(error).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {
                    System.out.println("*** Response Data *** "+response);

                    try {
                        JSONObject data = new JSONObject(response);
                        String access_token = data.getString("access_token");
                        String user_id = data.getString("userId");
                        String user_type = data.getString("userType");
                        String mobile_no = data.getString("mobileNo");

                        sharedPreferenceClass.setValue_string(StaticVariables.MOBILE_NUMBER
                                , data.getString("mobileNo"));
                        sharedPreferenceClass.setValue_string(StaticVariables.ACCESS_TOKEN
                                , data.getString("access_token"));
                        sharedPreferenceClass.setValue_string(StaticVariables.USER_ID
                                , data.getString("userId"));
                        sharedPreferenceClass.setValue_string(StaticVariables.USER_TYPE
                                , data.getString("userType"));
                        sharedPreferenceClass.setValue_boolean(StaticVariables.IS_REGISTERED
                                , true);

                        /*SharedPreferences userPref = getSharedPreferences("userCred", MODE_PRIVATE);
                        userPref.edit().putBoolean("isReg", true).apply();*/
                        if (sharedPreferenceClass.getValue_string(StaticVariables.USER_TYPE).equals("P"))
                            if (sharedPreferenceClass.getValue_boolean(StaticVariables.IS_VENDER_REGISTERED)) {

                                Intent intent = new Intent(WelcomeActivity.this, VenderProfileActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                Intent intent = new Intent(WelcomeActivity
                                        .this, ServiceSelectionActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        else if (sharedPreferenceClass.getValue_string(StaticVariables.USER_TYPE).equals("U"))
                            startActivity(new Intent(WelcomeActivity.this, UserActivity.class));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
