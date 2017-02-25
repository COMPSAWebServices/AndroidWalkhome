package com.compsawebservices.walkhome;

/**
 * Keeps track and updates the current user profile
 * Date: Dec 11th 2016
 * Version: 3.0
 */

public class UserProfile {
    private static String phonenumber;
    private static int currentStatus;//walkstatus

    public UserProfile(){}

    //updates the phonenumber
    public void updatePhonenumber(String phonenumber){
        this.phonenumber = phonenumber;
    }

    //returns the phone number
    public String getPhonenumber(){
        return phonenumber;
    }

    //stores the currentStatus of the walk
    public void setCurrentStatus(int status){
        this.currentStatus = status;
    }
    //returns current status of the walk
    public int getCurrentStatus(){
        return this.currentStatus;
    }
}//end UserProfile class
