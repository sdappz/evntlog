package com.swagger.app.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceCategoryModel {
    String category;
    String id;
    String imagePath;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static List<ServiceCategoryModel> populateFromDownload(String result){
        List<ServiceCategoryModel> sList= new ArrayList<>();
        if(result.equals("")|| result==null){

        }else{
            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i=0; i<jsonArray.length(); i++){
                    ServiceCategoryModel serviceCategoryModel = new ServiceCategoryModel();
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    serviceCategoryModel.setCategory(jsonObject.getString("category"));
                    serviceCategoryModel.setId(jsonObject.getString("id"));
                    serviceCategoryModel.setImagePath(jsonObject.getString("imagePath"));
                    sList.add(serviceCategoryModel);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return sList;
    }
}
