package com.mkretail.retail.Models;

public class DetailModel {

    String SMtitle;
    String imid;
    String price;
    String discount;
    String originalAmount;

    public String getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(String originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public DetailModel() {
    }

    public String getTitle() {
        return SMtitle;
    }

    public void setTitle(String title) {
        this.SMtitle = title;
    }

    public String getImid() {
        return imid;
    }

    public void setImid(String imid) {
        this.imid = imid;
    }
}
