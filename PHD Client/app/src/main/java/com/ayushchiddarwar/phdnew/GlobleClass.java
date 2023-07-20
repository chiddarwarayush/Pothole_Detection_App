package com.ayushchiddarwar.phdnew;

import android.app.Application;

public class GlobleClass extends Application {
private String user_id="";

public void setUserID(String str){
    user_id=str;
}
public String getUserID(){
    return user_id;
}

}
