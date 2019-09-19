package com.mkretail.retail.Models;

public class SubCatModel {

    private String SubCatID;
    private String SubCatName;

    public SubCatModel() {
    }

    public SubCatModel(String subCatID, String subCatName) {
        SubCatID = subCatID;
        SubCatName = subCatName;
    }

    public String getSubCatID() {
        return SubCatID;
    }

    public void setSubCatID(String subCatID) {
        SubCatID = subCatID;
    }

    public String getSubCatName() {
        return SubCatName;
    }

    public void setSubCatName(String subCatName) {
        SubCatName = subCatName;
    }
}
