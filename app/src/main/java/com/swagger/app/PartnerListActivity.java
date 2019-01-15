package com.swagger.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.swagger.app.adapter.PartnerAdapter;
import com.swagger.app.model.PartnerModel;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;

import java.util.List;

public class PartnerListActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView topTxt;
    RecyclerView partnerList;
    Activity mActivity;
    SharedPreferenceClass sharedPreferenceClass;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initWidgets();
        String serviceId = getIntent().getStringExtra("serviceId");
        if (serviceId != null) {
            if (Common.checkNetworkConnection(this)) {
                populatePartnerList(serviceId);
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initWidgets() {
        mActivity = PartnerListActivity.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);
        topTxt = findViewById(R.id.topText);
        partnerList = findViewById(R.id.serviceList);
        pBar = findViewById(R.id.pBar);
        topTxt.setText("Partner Details");
    }


    private void populatePartnerList(String serviceId) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        RequestParams params = new RequestParams();

        // add headers
        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type", "application/json");

        //params.put("productId", serviceId);
        client.get(Common.partnerListUrl + "/" + serviceId, new TextHttpResponseHandler() {
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
                Toast.makeText(PartnerListActivity.this, "Sorry, no partners available", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String response) {
                try {
                    List<PartnerModel> sList = PartnerModel.populateFromDownload(response);
                    if (sList.size() == 0) {
                        Toast.makeText(PartnerListActivity.this, "Sorry, unable to fetch list", Toast.LENGTH_SHORT).show();

                    } else {
                        final LinearLayoutManager llm = new LinearLayoutManager(PartnerListActivity.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        PartnerAdapter pAdapter = new PartnerAdapter(PartnerListActivity.this, sList);
                        partnerList.setLayoutManager(llm);
                        partnerList.setAdapter(pAdapter);
                        pBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
