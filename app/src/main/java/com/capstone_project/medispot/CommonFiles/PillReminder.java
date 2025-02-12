package com.capstone_project.medispot.CommonFiles;


import com.google.firebase.Timestamp;

public class PillReminder {
    String DrugName, DrugImgURL,ReferenceID, DrugQuantity,DrugInstructions, DrugSchedule, DrugType, Time, Date, DrugImgName;
    Timestamp Timestamp;

    public PillReminder(){}

    public PillReminder(String drugName, String drugImgURL, String referenceID,
            String drugQuantity, String drugInstructions,
            String drugSchedule, String drugType, String time, String date, String drugImgName,
            com.google.firebase.Timestamp timestamp)
    {
        DrugName = drugName;
        DrugImgURL = drugImgURL;
        ReferenceID = referenceID;
        DrugQuantity = drugQuantity;
        DrugInstructions = drugInstructions;
        DrugSchedule = drugSchedule;
        DrugType = drugType;
        Time = time;
        Date = date;
        Timestamp = timestamp;
        DrugImgName = drugImgName;
    }

    public String getDrugImgName() {
        return DrugImgName;
    }

    public void setDrugImgName(String drugImgName) {
        DrugImgName = drugImgName;
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

    public String getDrugQuantity() {
        return DrugQuantity;
    }

    public void setDrugQuantity(String drugQuantity) {
        DrugQuantity = drugQuantity;
    }

    public String getDrugInstructions() {
        return DrugInstructions;
    }

    public void setDrugInstructions(String drugInstructions) {
        DrugInstructions = drugInstructions;
    }

    public String getDrugSchedule() {
        return DrugSchedule;
    }

    public void setDrugSchedule(String drugSchedule) {
        DrugSchedule = drugSchedule;
    }

    public String getDrugType() {
        return DrugType;
    }

    public void setDrugType(String drugType) {
        DrugType = drugType;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


    public com.google.firebase.Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        Timestamp = timestamp;
    }
}
