package com.swagger.app.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PartnerModel {

    private String partnerName, serviceName, imagePath;
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static List<PartnerModel> populateFromDownload(String result) {
        List<PartnerModel> pList = new ArrayList<>();
        if (result.equals("") || result == null) {

        } else {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    PartnerModel partnerModel = new PartnerModel();
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    partnerModel.setId(jsonObject.getInt("id"));
                    partnerModel.setPartnerName(jsonObject.getString("partnerName"));
                    partnerModel.setServiceName(jsonObject.getString("productCategoryName"));
                    partnerModel.setImagePath(jsonObject.getString("imagePath"));
                    pList.add(partnerModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pList;
    }
}
