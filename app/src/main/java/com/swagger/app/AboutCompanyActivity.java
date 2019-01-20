package com.swagger.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.swagger.app.model.Service;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by imabh on 20-01-2019.
 */

public class AboutCompanyActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etCompanyName, etCompanyDescription, etWebsite, etServiceTime, etServiceLocation;
    Button btnSubmit;
    Activity mActivity;
    SharedPreferenceClass sharedPreferenceClass;
    AsyncHttpClient client = null;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_company);
        etCompanyName=(EditText)findViewById(R.id.et_cmpny_name);
        etCompanyDescription=(EditText)findViewById(R.id.et_description);
        etWebsite=(EditText)findViewById(R.id.et_website);
        etServiceTime=(EditText)findViewById(R.id.et_time);
        etServiceLocation=(EditText)findViewById(R.id.et_location);

        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        pBar = findViewById(R.id.pBar);

        mActivity = AboutCompanyActivity.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSubmit:

                if(etCompanyName.getText().toString().length()==0)
                {
                    Toast.makeText(mActivity, "Company name cannot be blank", Toast.LENGTH_SHORT).show();
                }
                else if(etCompanyDescription.getText().toString().length()==0)
                {
                    Toast.makeText(mActivity, "Company description cannot be blank", Toast.LENGTH_SHORT).show();
                }
                else if(etWebsite.getText().toString().length()==0)
                {
                    Toast.makeText(mActivity, "Company website cannot be blank", Toast.LENGTH_SHORT).show();
                }
                else if(etServiceTime.getText().toString().length()==0)
                {
                    Toast.makeText(mActivity, "Service time cannot be blank", Toast.LENGTH_SHORT).show();
                }
                else if(etServiceLocation.getText().toString().length()==0)
                {
                    Toast.makeText(mActivity, "Service location cannot be blank", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    insertCompanyDetails(etCompanyName.getText().toString().trim(),etCompanyDescription.getText().toString().trim(),
                            etWebsite.getText().toString().trim(),etServiceTime.getText().toString().trim(),etServiceLocation.getText().toString().trim());
                }

                break;
        }

    }

    private void insertCompanyDetails(String cmpnyName,String cmpnyDes,String website,String time,String location) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);

        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        JSONObject params = new JSONObject();

        try {
            params.put("name", cmpnyName);
            params.put("description",cmpnyDes);
            params.put("website",website);
            params.put("servicePin",location);
            params.put("serviceTime",time);
            params.put("mobileNo",sharedPreferenceClass.getValue_string(StaticVariables.MOBILE_NUMBER));
            params.put("userId",sharedPreferenceClass.getValue_string(StaticVariables.USER_ID));


            StringEntity entity = new StringEntity(params.toString());
            System.out.println("*** Request params *** "+params.toString());

            client.post(this, Common.partnerDetailsInsert, entity, "application/json", new TextHttpResponseHandler() {

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
                    Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                    System.out.println("*** Response Data *** "+response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String error = jsonObject.optString("error_description");

                        if (!error.isEmpty() && error != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setCancelable(true).setTitle(error).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                    try {
                        System.out.println("*** Response Data *** "+response);
                        JSONObject data = new JSONObject(response);
                        if(data.getString("status").equalsIgnoreCase("true")) {
                            sharedPreferenceClass.setValue_boolean(StaticVariables.IS_VENDER_REGISTERED
                                    , true);
                            Toast.makeText(mActivity, "Vender added successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mActivity, VenderProfileActivity.class);
                            startActivity(intent);
                            finish();

                        }

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
