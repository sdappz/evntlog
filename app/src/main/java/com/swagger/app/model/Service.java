package com.swagger.app.model;

/**
 * Created by imabh on 12-01-2019.
 */

public class Service {

     public String category="";
     public String cat_id="";

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }
    @Override
    public String toString() {
        return this.category;
    }
}
