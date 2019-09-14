package com.ancologi.applications.bloemb.Activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.R;


public class ReachUsActivity extends MasterActivity {

    TextView cMobile,cPhone,cMessenger,cTelegram,cWebsite,cWhatsapp,cEmail;
    String sMobile,sPhone,sMessenger,sTelegram,sWebsite,sWhatsapp,sEmail;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_reach_us);
        super.onCreate(savedInstanceState);
        assignUIReferences();
        getData();
        showData();
        assignActions();
    }

    @Override
    protected void assignUIReferences() {
        cMobile = findViewById(R.id.cMobile);
        cPhone = findViewById(R.id.cPhone);
        cMessenger = findViewById(R.id.cMessenger);
        cTelegram = findViewById(R.id.cTelegram);
        cWebsite = findViewById(R.id.cWebsite);
        cWhatsapp = findViewById(R.id.cWhatsapp);
        cEmail = findViewById(R.id.cEmail);
    }

    @Override
    protected void showData() {
        cMobile.setText(sMobile);
        cPhone.setText(sPhone);
        cMessenger.setText(sMessenger);
        cTelegram.setText(sTelegram);
        cWebsite.setText(sWebsite);
        cWhatsapp.setText(sWhatsapp);
        cEmail.setText(sEmail);
    }

    @Override
    protected void getData() {
        sMobile = readSharedPreferenceString("sMobile");
        sPhone = readSharedPreferenceString("sPhone");
        sMessenger = readSharedPreferenceString("sMessenger");
        sTelegram = readSharedPreferenceString("sTelegram");
        sWebsite = readSharedPreferenceString("WebSiteUrl");
        sWhatsapp = readSharedPreferenceString("sWhatsapp");
        sEmail = readSharedPreferenceString("sEmail");
    }

    @Override
    protected void assignActions() {
        toolbar = set_toolbar(true, getResources().getString(R.string.contact));

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
}
