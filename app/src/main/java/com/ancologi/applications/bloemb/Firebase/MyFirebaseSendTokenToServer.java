package com.ancologi.applications.bloemb.Firebase;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

/**
 * Created by Ya7ya on 10/31/2018.
 */

public class MyFirebaseSendTokenToServer
{
    private int UserId=0;
    private String Token = "",FCM_Token="";
    private Context myContext;

    public MyFirebaseSendTokenToServer(Context context,int userId,String token,String fCM_Token)
    {
        UserId = userId;
        Token = token;
        FCM_Token = fCM_Token;
        myContext = context;
    }

    public void Execute()
    {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("UserId", UserId);
            jsonParam.put("Token", Token);
            jsonParam.put("Platform", "android");
            jsonParam.put("FCM_Token", FCM_Token);
        }
        catch (Exception e) {}


    }


    //write shared preferences in String
    private void writeSharedPreferenceFCMInt(String key , int value) {

        SharedPreferences sharedPreferences = myContext.getSharedPreferences(MyFirebaseMessagingService.APP_PREFS_FCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
}