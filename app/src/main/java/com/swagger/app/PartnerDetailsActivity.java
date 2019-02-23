package com.swagger.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class PartnerDetailsActivity extends AppCompatActivity {

    TextView partnerNameTxt, partnerServiceTxt,partnerPhoneTxt;
    ImageView partnerImg,secondImg;
    Toolbar toolbar;
    AsyncHttpClient client = null;
    ProgressBar pBar;
    SharedPreferenceClass sharedPreferenceClass;
    Activity mActivity;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partner_detail_layout);
        toolbar = findViewById(R.id.toolbar);
        mActivity = PartnerDetailsActivity.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);
        pBar = findViewById(R.id.pBar);

        setSupportActionBar(toolbar);
        initWidgets();

    }

    private void initWidgets() {
        partnerNameTxt = findViewById(R.id.partnerNameTxt);
        partnerServiceTxt = findViewById(R.id.partnerServiceTxt);
        partnerPhoneTxt=findViewById(R.id.partnerPhoneTxt);
        partnerImg=findViewById(R.id.partnerImg);
        secondImg=findViewById(R.id.secondImg);
        pBar = findViewById(R.id.pBar);

         id=getIntent().getIntExtra("id",0);
          fetchPartnerDetails();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void fetchPartnerDetails() {
        client = new AsyncHttpClient();
        client.setTimeout(30000);

        // add headers
        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type", "application/json");


        client.get(mActivity, Common.partnerDetailsFetchUrl + id, new TextHttpResponseHandler() {

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
            }

            @Override
            public void onSuccess(int i, Header[] headers, String response) {

                System.out.println("****** Response for document fetching ******" + response);

                try {
                        JSONObject data = new JSONObject(response);
                        partnerNameTxt.setText(data.getString("name"));
                        partnerServiceTxt.setText(data.getString("description"));
                        partnerPhoneTxt.setText(data.getString("mobileNo"));
                        String cover_img_path = data.getString("coverPicturePath");
                        String profile_img_path=data.getString("profilePicturePath");
                        if (!cover_img_path.equals("")) {
                            Picasso.get().load("http://"+cover_img_path).into(partnerImg);
                        }
                    if (!profile_img_path.equals("")) {
                        Picasso.get().load("http://"+profile_img_path).into(secondImg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
