package com.mkretail.retail.Models;

import java.util.ArrayList;

public class Model {

    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int MULTI_TYPE=2;

    public int type;
    public String image;
    public String text;
    public String myid;
    public String linktype;
    public String cid;
    public String ncid;
    public String label;

    private String headerTitle;
    private ArrayList<DashProductModel> allItemsInSection;

    public String getCid() {
        return cid;
    }

    public String getMyid() {
        return myid;
    }

    public String getLinktype() {
        return linktype;
    }

    public Model() {
    }

    public String getNcid() {
        return ncid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Model(int type, String image, String myid, String linktype, String cid) {
        this.type = type;
        this.image = image;
        this.myid = myid;
        this.linktype = linktype;
        this.cid = cid;
    }


    public Model(String text, int type, String myid, String linktype, String cid) {
        this.type = type;
        this.text = text;
        this.myid = myid;
        this.linktype = linktype;
        this.cid = cid;
    }

    public Model(String headerTitle, ArrayList<DashProductModel> allItemsInSection, int type, String ncid) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
        this.type = type;
        this.ncid = ncid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<DashProductModel> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<DashProductModel> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}
