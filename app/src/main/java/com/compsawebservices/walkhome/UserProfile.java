package com.compsawebservices.walkhome;

/**
 * Created by Ly on 2016-10-11.
 */

public class UserProfile {

    private static String phonenumber;
    //TODO transition from bundle to class later
//    private static String latFrom;
//    private static String longFrom;
//    private static String latTo;
//    private static String longTo;


    public UserProfile(){    }


    public void updatePhonenumber(String phonenumber){
        this.phonenumber = phonenumber;
    }
    public String getPhonenumber(){
        return phonenumber;
    }
}
