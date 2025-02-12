package com.capstone_project.medispot.CommonFiles;


import com.google.firebase.Timestamp;

public class savedDrugs {
    String DrugName, DrugImgURL,ReferenceID, ReferenceInDrugList, Date;
    Timestamp Timestamp;

    public savedDrugs(){}

    public savedDrugs(String drugName, String drugImgURL, String referenceID, String referenceInDrugList, String date, Timestamp timestamp) {
        DrugName = drugName;
        DrugImgURL = drugImgURL;
        ReferenceID = referenceID;
        ReferenceInDrugList = referenceInDrugList;
        Timestamp = timestamp;
        Date = date;
    }

    public Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        Timestamp = timestamp;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDrugName() {
        return DrugName;
    }

    public void setDrugName(String drugName) {
        DrugName = drugName;
    }

    public String getDrugImgURL() {
        return DrugImgURL;
    }

    public void setDrugImgURL(String drugImgURL) {
        DrugImgURL = drugImgURL;
    }

    public String getReferenceID() {
        return ReferenceID;
    }

    public void setReferenceID(String referenceID) {
        ReferenceID = referenceID;
    }

    public String getReferenceInDrugList() {
        return ReferenceInDrugList;
    }

    public void setReferenceInDrugList(String referenceInDrugList) {
        ReferenceInDrugList = referenceInDrugList;
    }
}
