package com.capstone_project.medispot.CommonFiles;

public class drugs {
    String DrugName,BrandName,GenericName,Description,Directions,Dosage,
            Precautions,SideEffects, Warnings,FrontImage, BackImage, PilImage, Document_ReferenceID;

    public drugs(){}

    public drugs(String drugName, String document_ReferenceID,String brandName,
                 String genericName, String description, String directions,String dosage,
                 String precautions,String sideEffects,String warnings, String frontImage,
                 String backImage, String pilImage) {

        DrugName = drugName;
        Document_ReferenceID = document_ReferenceID;
        BrandName = brandName;
        GenericName = genericName;
        Description = description;
        Directions = directions;
        Dosage = dosage;
        Precautions = precautions;
        SideEffects = sideEffects;
        Warnings = warnings;
        FrontImage = frontImage;
        BackImage = backImage;
        PilImage = pilImage;
    }

    public String getDrugName() {
        return DrugName;
    }

    public void setDrugName(String drugName) {
        DrugName = drugName;
    }

    public String getDocument_ReferenceID() {
        return Document_ReferenceID;
    }

    public void setDocument_ReferenceID(String document_ReferenceID) {
        Document_ReferenceID = document_ReferenceID;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getGenericName() {
        return GenericName;
    }

    public void setGenericName(String genericName) {
        GenericName = genericName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDirections() {
        return Directions;
    }

    public void setDirections(String directions) {
        Directions = directions;
    }

    public String getDosage() {
        return Dosage;
    }

    public void setDosage(String dosage) {
        Dosage = dosage;
    }

    public String getPrecautions() {
        return Precautions;
    }

    public void setPrecautions(String precautions) {
        Precautions = precautions;
    }

    public String getSideEffects(){
        return SideEffects;
    }

    public void setSideEffects(String sideEffects) {
        SideEffects = sideEffects;
    }

    public String getWarnings() {
        return Warnings;
    }

    public void setWarnings(String warnings) {
        Warnings = warnings;
    }

    public String getFrontImage() {
        return FrontImage;
    }

    public void setFrontImage(String frontImage) {
        FrontImage = frontImage;
    }

    public String getBackImage() {
        return BackImage;
    }

    public void setBackImage(String backImage) {
        BackImage = backImage;
    }

    public String getPilImage() {
        return PilImage;
    }

    public void setPilImage(String pilImage) {
        PilImage = pilImage;
    }

}
