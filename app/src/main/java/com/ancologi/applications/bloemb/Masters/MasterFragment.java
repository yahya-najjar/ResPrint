package com.ancologi.applications.bloemb.Masters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;


import com.ancologi.applications.bloemb.R;
import com.ancologi.applications.bloemb.Utils.LoadingDialog;

import java.util.ArrayList;
import java.util.Locale;

import static com.ancologi.applications.bloemb.Masters.MasterActivity.APP_PREFS;


public abstract class MasterFragment extends Fragment {

    private View view;
    private int layoutId;
    private LoadingDialog mLoadingDialog;

    Context context;
    ArrayList<Integer> ids = new ArrayList<Integer>();
    public Locale locale = new Locale("ar","SA");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    ///////////////////
    //Read from Shared Preferance (INTEGER)
    public int readSharedPreferenceInt(String key) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    public String readSharedPreferenceString(String key){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    ///////////////////

    //write shared preferences in integer
    public void writeSharedPreferenceInt(String key , int value) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
    //write shared preferences in String
    public void writeSharedPreferenceString(String key , String value ){

        SharedPreferences sharedPrefereSt = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();

        editor.putString(key, value);
        editor.commit();
    }



    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    protected void showSnackBarMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.dismiss), view -> {
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }


}