package com.mkretail.retail.Models;

import android.widget.ArrayAdapter;

public class DashProductModel {

    private String title;
    //private String shortdesc;
    private ArrayAdapter<String> dropdown;
    private String imid;
    private String image;
    private String price;
    private String originalAmount;
    private String wishlistid;
    private String isExpress;
    private String inStock;

    public DashProductModel(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public DashProductModel(String title, String imid, String image, String description, String price, String isExpress, String inStock) {
        this.title = title;
        this.imid = imid;
        this.image = image;
        this.price = price;
        Description = description;
        this.isExpress = isExpress;
        this.inStock = inStock;
//        this.amount = amount;
    }

    public String getInStock() {
        return inStock;
    }

    public void setInStock(String inStock) {
        this.inStock = inStock;
    }

    public String getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(String originalAmount) {
        this.originalAmount = originalAmount;
    }

    public void setIsExpress(String isExpress) {
        this.isExpress = isExpress;
    }

    public String getIsExpress() {
        return isExpress;
    }

    public String getWishlistid() {
        return wishlistid;
    }

    public void setWishlistid(String wishlistid) {
        this.wishlistid = wishlistid;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    private String Description;


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public DashProductModel() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDropdown(ArrayAdapter<String> dropdown) {
        this.dropdown = dropdown;
    }

    public void setImid(String discount) {
        this.imid = discount;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getTitle() {
        return title;
    }

   /* public String getShortdesc() {
        return shortdesc;
    }*/

    public ArrayAdapter<String> getDropdown() {
        return dropdown;
    }

    public String getImid() {
        return imid;
    }

    public String  getImage() {
        return image;
    }
}
