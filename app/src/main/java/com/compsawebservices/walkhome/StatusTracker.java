package com.compsawebservices.walkhome;

/**
 * Created by Ly on 2016-10-11.
 */

public class StatusTracker {

    private static int statusCount;

    public StatusTracker(){
        this.statusCount = 0;
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
