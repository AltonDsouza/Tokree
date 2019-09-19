package com.mkretail.retail.Models;

public class OfferModel {

    String image;
    String offertext;
    String offerTitle;

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }


    public String getOffertext() {
        return offertext;
    }

    public void setOffertext(String offertext) {
        this.offertext = offertext;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public OfferModel() {
    }
}
