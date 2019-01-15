package com.swagger.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.swagger.app.adapter.ServiceAdapter;
import com.swagger.app.model.ServiceCategoryModel;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;

import java.util.List;

public class UserActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView serviceList;
    ProgressBar pBar;
    SharedPreferenceClass sharedPreferenceClass;
    Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initWidgets();
        if (Common.checkNetworkConnection(this)) {
            populateServiceList();
        } else {
            Toast.makeText(this, "Please check internet connection & try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void initWidgets() {
        mActivity = UserActivity.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);
        serviceList = findViewById(R.id.serviceList);
        pBar = findViewById(R.id.pBar);
    }

    private void populateServiceList() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);

        // add headers
        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type", "application/json");

        client.get(Common.productCategoryUrl, new TextHttpResponseHandler() {
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
                Toast.makeText(UserActivity.this, "Sorry, Something went wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String response) {
                try {
                    List<ServiceCategoryModel> sList = ServiceCategoryModel.populateFromDownload(response);
                    if (sList.size() == 0) {
                        Toast.makeText(UserActivity.this, "Sorry, unable to fetch list", Toast.LENGTH_SHORT).show();

                    } else {
                        final GridLayoutManager glm = new GridLayoutManager(UserActivity.this, 2, LinearLayoutManager.HORIZONTAL, false);
                        glm.setOrientation(LinearLayoutManager.VERTICAL);
                        ServiceAdapter pAdapter = new ServiceAdapter(UserActivity.this, sList);
                        serviceList.setLayoutManager(glm);
                        serviceList.setAdapter(pAdapter);
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
