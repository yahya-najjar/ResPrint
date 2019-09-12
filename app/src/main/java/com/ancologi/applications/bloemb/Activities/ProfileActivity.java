package com.ancologi.applications.bloemb.Activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.R;


/**
 * Created by Ya7ya on 11/3/2018.
 */

public class ProfileActivity extends MasterActivity
{
    String company_info;
    TextView info,title;
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_company_profile);
        super.onCreate(savedInstanceState);
        assignUIReferences();
        company_info = (String) getIntent().getStringExtra("company");

        assignActions();
        switch (company_info){
            case "policy":
                title.setText(getResources().getString(R.string.policy));
                info.setText(getResources().getString(R.string.policy_info));
                break;
            case "about":
                title.setText(getResources().getString(R.string.company_title));
                info.setText(getResources().getString(R.string.about_us_info));
                break;
            case "terms":
                title.setText("Overview");
                break;

        }
    }

    @Override
    protected void assignActions() {
        String title = (company_info.equals("policy" ) ? getString(R.string.policy) : getString(R.string.company_title));
        toolbar = set_toolbar(true, title);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("content","pressed");
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void getData() {
         company_info = (String) getIntent().getStringExtra("company");

    }

    @Override
    protected void showData() {

    }


    @Override
    protected void assignUIReferences() {
        title = (TextView) findViewById(R.id.company_title);
        info = (TextView) findViewById(R.id.company_info);
    }

//    public  void getDataExtra(){
//        JSONObject jsonParam = new JSONObject();
//
//        try {
//            jsonParam.put("term", company_info);
//        }
//        catch (Exception e) {}
//
//        //call the OnlineTrucks api to get list of Truck
//        new SendGetJsonApi(this, "company_profile", jsonParam, new CallBackListener() {
//            @Override
//            public void onFinish(String response) {
//                String result="",terms="";
//                try {
//                    JSONObject jsonin = new JSONObject(response);
//                    result = jsonin.optString("result");
//                    Log.d("resultt",result);
//                    if (result.equals("success")){
//                        Log.d("content","user checked");
//                        terms = jsonin.optString("content");
//                        info.setText(terms);
//                    }
//                    else {
//                        int error_code = jsonin.optInt("error_code");
//                        String error_des = jsonin.optString("error_des");
//
//                        switch (error_code){
//                            case 1:
//                                if(!error_des.equals("")){
//                                    Log.d("contentt", error_des);
//                                    Toast.makeText(getBaseContext(), error_des , Toast.LENGTH_LONG).show();
//                                }else {
//                                    Log.d("content", "not success");
//                                }
//                                break;
//                            case -1:
//                                Toast.makeText(getBaseContext(), "You have to login again " , Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onProgress(int process) {}
//        }).Execute();
//
//    }
}
