package com.compsawebservices.walkhome;

/**
 * Created by Ly on 2016-10-11.
 */

public class StatusTracker {

    private static int statusCount = 1 ;

    public StatusTracker(){
        if (statusCount==1){
            this.statusCount = 1;
        }

    }

    public void updateStatus(int status){
        this.statusCount = status;
    }

    public void updateCount(){
        statusCount++;
    }

    public void resetCount(){
        statusCount = 0 ;
    }

    public int getCount(){
        return statusCount;
    }

}
