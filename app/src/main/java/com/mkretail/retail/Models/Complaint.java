package com.mkretail.retail.Models;

public class Complaint {
    public String ComplaintID;
    public String message;
    public String complaintTitle;

    public Complaint(String complaintID, String complaintTitle) {
        ComplaintID = complaintID;
        this.complaintTitle = complaintTitle;
    }

    public String getComplaintID() {
        return ComplaintID;
    }

    public String getMessage() {
        return message;
    }

    public String getComplaintTitle() {
        return complaintTitle;
    }
}
