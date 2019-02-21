package com.swagger.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PartnerDetailsActivity extends AppCompatActivity {

    TextView partnerNameTxt, partnerServiceTxt;
    Toolbar toolbar;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partner_detail_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initWidgets();

    }

    private void initWidgets() {
        partnerNameTxt = findViewById(R.id.partnerNameTxt);
        partnerServiceTxt = findViewById(R.id.partnerServiceTxt);
        pBar = findViewById(R.id.pBar);

        String partnerName = getIntent().getStringExtra("partnerName");
        String partnerService = getIntent().getStringExtra("partnerService");

        partnerNameTxt.setText(partnerName);
        partnerServiceTxt.setText(partnerService);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
