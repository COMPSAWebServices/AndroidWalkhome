package com.compsawebservices.walkhome;

/**
 * Created by Ly on 2016-10-11.
 */

public class UserProfile {

    private static String phonenumber;
    public UserProfile(String phonenumberString){
        this.phonenumber = phonenumberString;
    }

    public String getPhonenumber(){
        return phonenumber;
    }
}
