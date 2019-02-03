package com.swagger.app;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by imabh on 20-01-2019.
 */

public class VenderProfileActivity extends AppCompatActivity {
    Activity mActivity;
    SharedPreferenceClass sharedPreferenceClass;
    ProgressBar pBar;


    ImageView imgProfile,img_background;
    TextView tv_identity_verify,tv_additional_services,tv_past_work_details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vender_profile_page);
        mActivity = VenderProfileActivity.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);
        pBar = findViewById(R.id.pBar);

        img_background = (ImageView) findViewById(R.id.img_background);
        tv_identity_verify = findViewById(R.id.tv_identity_verify);


        imgProfile=(ImageView)findViewById(R.id.imgProfile);
        img_background=(ImageView)findViewById(R.id.img_background);
        tv_identity_verify=findViewById(R.id.tv_identity_verify);
  //      tv_additional_services=findViewById(R.id.tv_additional_services);
        tv_past_work_details=(TextView)findViewById(R.id.tv_past_work_details);

        tv_identity_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, VenderIdentityVerification.class);
                intent.putExtra("PastWorkDetails","false");
                startActivity(intent);
            }
        });
      /*  tv_additional_services.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, ServiceSelectionActivity.class);
            intent.putExtra("AdditionalServices","true");
            startActivity(intent);
        });*/
     
        tv_past_work_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, VenderIdentityVerification.class);
                intent.putExtra("PastWorkDetails","true");
                startActivity(intent);
            }
        });

        apiPartnerDetailsGetByID();

        tv_identity_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, VenderIdentityVerification.class);
                startActivity(intent);
            }
        });

    }

    private void apiPartnerDetailsGetByID() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(30000);


        // add headers
        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type", "application/json");

        System.out.println("*** Request **** " + Common.partnerDetailsGetById + "/" + sharedPreferenceClass.getValue_string(StaticVariables.USER_ID));
        client.get(Common.partnerDetailsGetById + "/" + sharedPreferenceClass.getValue_string(StaticVariables.USER_ID), new TextHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                pBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pBar.setVisibility(View.GONE);

                return;
            }

            @Override
            public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                System.out.println("****** Response ******" + response);
                Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String response) {

                System.out.println("****** Response ******" + response);
                try {
                    JSONObject data = new JSONObject(response);
                    String image_path = data.getString("imagePath");
                    System.out.println("***image path *****" + image_path);
                    if (image_path.equalsIgnoreCase("null")) {
                        imgProfile.setImageResource(R.mipmap.logo);
                        img_background.setImageResource(R.mipmap.ic_launcher);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
