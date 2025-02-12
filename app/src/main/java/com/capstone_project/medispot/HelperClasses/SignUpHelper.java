package com.capstone_project.medispot.HelperClasses;

public class SignUpHelper {
    String Name, Email, Phone, DOB, Password;

    public SignUpHelper() {}

    public SignUpHelper(String Name, String DOB, String Phone, String Email, String Password) {
        this.Name = Name;
        this.Phone = Phone;
        this.Email = Email;
        this.DOB = DOB;
        this.Password = Password;
    }

    public String getName() {return Name;}
    public void setName(String Name) { this.Name = Name;}

    public String getPhone() {return Phone;}
    public void setPhone(String Phone) {this.Phone = Phone;}


    public String getEmail() {return Email;}
    public void setEmail(String Email) { this.Email = Email;}


    public String getDob() { return DOB;}
    public void setDob(String DOB) {this.DOB = DOB;}


    public String getPassword() {return Password;}
    public void setPassword(String Password) {this.Password = Password;
    }
}
