package com.swagger.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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

import java.util.ArrayList;

/**
 * Created by imabh on 11-01-2019.
 */

public class ServiceSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spnrServices;
    Button btnSubmit;
    String data[]={"---- SELECT SERVICE ---","WEDDING PLANNER" };
    SharedPreferenceClass sharedPreferenceClass;
    AsyncHttpClient client = null;
    ArrayList<Service> al_services;
    String selected_cat_id = "";
    ProgressBar pBar;
    ImageView iv_add;
    EditText addedServicesTxt;
    Activity mActivity;
    String addedServices="",selected_category="";
    ArrayList<String> al_catID;
    LinearLayout ll_additional_services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vender_service_selection);

        mActivity = ServiceSelectionActivity.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);
        spnrServices=(Spinner)findViewById(R.id.spnrServices);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        pBar = findViewById(R.id.pBar);
        iv_add=(ImageView)findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);
        addedServicesTxt=(EditText)findViewById(R.id.et_added_services);
        ll_additional_services=(LinearLayout)findViewById(R.id.ll_additional_services);

        al_catID= new ArrayList<String>();

        System.out.println("***** Access Token ******"+sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        getServiceList();


    }
    public void setServices(){
        ArrayAdapter adapter = new ArrayAdapter(mActivity, R.layout.my_spinner_textview, al_services);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spnrServices.setAdapter(adapter);
        spnrServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                    selected_cat_id = al_services.get(i).getCat_id();
                    selected_category = al_services.get(i).getCategory();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void getServiceList() {
        client = new AsyncHttpClient();

        client.setTimeout(30000);


        // add headers
        client.addHeader("Authorization", "bearer "+sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type","application/json");


        client.get(mActivity, Common.serviceListUrl, new TextHttpResponseHandler() {



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
                    Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                    System.out.println("****** Response for category list ******"+response);

                    try {
                        al_services=new ArrayList<Service>();
                        JSONArray jArray = null;
                        try {
                            jArray = new JSONArray(response);
                            for (int a = 0; a < jArray.length(); a++) {
                                JSONObject jsonOb = jArray.getJSONObject(a);
                                Service objService = new Service();
                                objService.setCat_id(jsonOb.getString("id"));
                                objService.setCategory(jsonOb.getString("category"));
                                al_services.add(objService);

                            }
                            if(al_services.size()>0)
                            {

                              setServices();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_add:
                addedServices=addedServices+selected_category;
                al_catID.add(selected_cat_id);
                addedServicesTxt.setText(addedServices);
                break;

            case R.id.btnSubmit:
                if(ll_additional_services.getVisibility()==View.VISIBLE)
                {
                    System.out.println("**** Array list*****"+al_catID);
                    apiInsertAdditionalPartnerProductListing(al_catID);
                }
                else
                {
                    apiInsertPartnerProductListing(selected_cat_id);
                }

                break;
        }

    }

    public void apiInsertAdditionalPartnerProductListing(ArrayList<String> al)
    {
        client = new AsyncHttpClient();
        client.setTimeout(30000);

        client.addHeader("Authorization", "bearer "+sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        System.out.println("**** Array list*****"+al);

        try {

            JSONArray jarray=new JSONArray();

            for(int j=0;j<al.size();j++){
                JSONObject jsonParams = new JSONObject();
                    System.out.println("**** Array list*****"+al.get(j));
                    jsonParams.put("productCategoryId",al.get(j));
                    jsonParams.put("userId",sharedPreferenceClass.getValue_string(StaticVariables.USER_ID));
                    jsonParams.put("isDefault","0");
                    jarray.put(jsonParams);
            }

            System.out.println("**** JSONARRAY ****"+jarray.toString());

            StringEntity entity = new StringEntity(jarray.toString());

            client.post(this, Common.partnerWiseProductInsert, entity, "application/json", new TextHttpResponseHandler() {

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

                    System.out.println("*** Response ***"+response);
                    Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                 System.out.println("****** Response for partner wise product insert *****"+response);
                    Toast.makeText(mActivity, "Additional Services added Successfully !", Toast.LENGTH_SHORT).show();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void apiInsertPartnerProductListing(String cat_id)
    {
        client = new AsyncHttpClient();
        client.setTimeout(30000);

        client.addHeader("Authorization", "bearer "+sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));


        try {

            JSONArray jarray=new JSONArray();
                JSONObject jsonParams = new JSONObject();

                jsonParams.put("productCategoryId",cat_id);
                jsonParams.put("userId",sharedPreferenceClass.getValue_string(StaticVariables.USER_ID));
                jsonParams.put("isDefault","0");
                jarray.put(jsonParams);


            System.out.println("**** JSONARRAY ****"+jarray.toString());

            StringEntity entity = new StringEntity(jarray.toString());

            client.post(this, Common.partnerWiseProductInsert, entity, "application/json", new TextHttpResponseHandler() {

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

                    System.out.println("*** Response ***"+response);
                    Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                    System.out.println("****** Response for partner wise product insert *****"+response);
                    Toast.makeText(mActivity, "Service added Successfully !", Toast.LENGTH_SHORT).show();



                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}



