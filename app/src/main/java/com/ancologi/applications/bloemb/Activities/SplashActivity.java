package com.ancologi.applications.bloemb.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ancologi.applications.bloemb.BuildConfig;
import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.R;
import com.ancologi.applications.bloemb.Utils.UserUtils;
import com.ancologi.applications.bloemb.Utils.Utils;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;


public class SplashActivity extends MasterActivity {
    public static ImageView imageView;
    private String deviceId;
    private int launched_before =0;
    int versionCode = BuildConfig.VERSION_CODE;
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);

        Set unread_orders = new HashSet<>();
        unread_orders = readSharedPreferenceStringSet("unread_orders");

        mUnread_orders = unread_orders;


        Log.d("unread_orders",String.valueOf(unread_orders));
        Log.d("unread_orders_m",String.valueOf(mUnread_orders));

        FirebaseMessaging.getInstance().subscribeToTopic("all-users")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("SUBSCRIPTION","FAILURE");
                        }else{
                            Log.d("SUBSCRIPTION","SUCCESS");
                        }
                    }
                });


        writeSharedPreferenceInt("budges_count",0);

        try {
            Badges.removeBadge(getApplicationContext());
            // Alternative way
            Badges.setBadge(getApplicationContext(), 0);
        } catch (BadgesNotSupportedException badgesNotSupportedException) {
            Log.d(TAG, badgesNotSupportedException.getMessage());
        }


        CheckVersion();
        super.onCreate(savedInstanceState);

        try {
            hideSystemUI();
        } catch (Exception e) {
            e.printStackTrace();
        }







//        Resources res = getApplicationContext().getResources();
//        // Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        android.content.res.Configuration conf = res.getConfiguration();
//        conf.setLocale(new Locale("ar")); // API 17+ only.
//        res.updateConfiguration(conf, dm);

        launched_before = readSharedPreferenceInt("launched_before");
        if (launched_before == 0){
            writeSharedPreferenceInt("launched_before",1);
            Utils.setAppLocale("ar", SplashActivity.this);
            UserUtils.setApplicationLanguage(getApplicationContext(), "ar");
            writeSharedPreferenceString("app_locale","ar");
        }
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            finish();

                    }
                },
                1000
        );
    }

    private void hideSystemUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void showData() {

    }

    @Override
    protected void assignUIReferences() {

    }

    @Override
    protected void assignActions() {

    }

    /////////// Call Ws for check Version
    public void CheckVersion()
    {
        JSONObject jsonParam = new JSONObject();

//        new SendGetJsonApi(this,"check",jsonParam,
//                new CallBackListener() {
//                    @Override
//                    public void onFinish(String resultjson)
//                    {
//                        try {
//                            // Create the root JSONObject from the JSON string.
//                            JSONObject jsonin = new JSONObject(resultjson);
//                            String result = jsonin.optString("result");
//                            if (result.equals("success"))
//                            {
//                                doCheckVersion(jsonin.optString("content"));
//                                Log.d("checkApi",String.valueOf(jsonin.optString("content")));
//                            }
//                        }
//                        catch ( Exception e) {}
//                    }
//
//                    @Override
//                    public void onProgress(int process) {
//                    }
//                }
//
//        ).Execute();

    }

    public void doCheckVersion(String resultVersion)
    {
        int PNumber = 0;
        String google_downloadurl = "";
        String website_downloadurl = "";
        String whatsapp = "";
        String telegram = "";
        String facebook = "";
        String phone = "";
        String mobile = "";
        String email = "";

        try {
            // Create the root JSONObject from the JSON string.
            JSONObject contentjson = new JSONObject(resultVersion);
            try {
                PNumber = contentjson.optInt("android-version");
            } catch (Exception e) {
            }
            google_downloadurl = contentjson.optString("GoogleUrl");
            website_downloadurl = contentjson.optString("WebSiteUrl");
            whatsapp = contentjson.optString("whatsapp");
            facebook = contentjson.optString("facebook");
            mobile = contentjson.optString("mobile");
            phone = contentjson.optString("phone");
            telegram = contentjson.optString("telegram");
            email = contentjson.optString("email");
            JSONArray jArray = contentjson.optJSONArray("countries");
            if (jArray != null) {
                Log.d("content", "0:");
                Log.d("content", String.valueOf(jArray));

            }

            writeSharedPreferenceString("GoogleUrl",google_downloadurl);
            writeSharedPreferenceString("WebSiteUrl",website_downloadurl);
            writeSharedPreferenceString("sWhatsapp",whatsapp);
            writeSharedPreferenceString("sMessenger",facebook);
            writeSharedPreferenceString("sMobile",mobile);
            writeSharedPreferenceString("sPhone",phone);
            writeSharedPreferenceString("sTelegram",telegram);
            writeSharedPreferenceString("sEmail",email);


        }
        catch ( Exception e) {
            Log.d("Exception_handler",String.valueOf(e));
        }


        if (PNumber > 0 && !website_downloadurl.equals("") && !google_downloadurl.equals(""))
        {
            if (PNumber > versionCode && !SplashActivity.this.isFinishing()) {
                //tp.setText("التطبيق بحاجة لتحديث");
                //  verNum = resws;
                Log.d("google_link","Pnum");

                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setCancelable(false);
                //dialog.setTitle("يجب تحديث التطبيق");
                TextView title = new TextView(SplashActivity.this);
                // You Can Customise your Title here
                title.setText(getString(R.string.app_needs_to_be_update));
                //title.setBackgroundColor(Color.DKGRAY);
                title.setPadding(10, 20, 10, 0);
                title.setGravity(Gravity.CENTER);
                //title.setTextColor(Color.WHITE);
                title.setTextSize(20);


                dialog.setCustomTitle(title);
                dialog.setMessage(getString(R.string.do_you_want_to_update_now));

                View dialogview = getLayoutInflater().inflate(R.layout.update_dialog, null);
                dialog.setView(dialogview);

                final RadioButton radioGoogle = (RadioButton) dialogview.findViewById(R.id.radioGoogle);
                final RadioButton radioWebSite = (RadioButton) dialogview.findViewById(R.id.radioWebSite);

                final String google_downloadurl_final = google_downloadurl;
                final String website_downloadurl_final = website_downloadurl;
                dialog.setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (radioGoogle.isChecked()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(google_downloadurl_final));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website_downloadurl_final));
                            startActivity(intent);
                        }

                    }
                });
                dialog.setNegativeButton(getString(R.string.later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                        dialog.cancel();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
            }

        }

    }


}
