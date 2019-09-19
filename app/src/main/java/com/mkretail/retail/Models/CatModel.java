package com.mkretail.retail.Models;

public class CatModel {

    private String CatID;
    private String CatName;
    private String CatImageURLTrim;

    public CatModel() {
    }

    public CatModel(String catID, String catName, String catImageURLTrim) {
        CatID = catID;
        CatName = catName;
        CatImageURLTrim = catImageURLTrim;
    }

    public String getCatID() {
        return CatID;
    }

    public void setCatID(String catID) {
        CatID = catID;
    }

    public String getCatName() {
        return CatName;
    }

    public void setCatName(String catName) {
        CatName = catName;
    }

    public String getCatImageURLTrim() {
        return CatImageURLTrim;
    }

    public void setCatImageURLTrim(String catImageURLTrim) {
        CatImageURLTrim = catImageURLTrim;
    }

}
