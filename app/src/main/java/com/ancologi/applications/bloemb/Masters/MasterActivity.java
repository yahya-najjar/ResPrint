package com.ancologi.applications.bloemb.Masters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;


import com.ancologi.applications.bloemb.Activities.ProfileActivity;
import com.ancologi.applications.bloemb.Activities.ReachUsActivity;
import com.ancologi.applications.bloemb.Api.CallBackListener;
import com.ancologi.applications.bloemb.Api.SendGetJsonApi;
import com.ancologi.applications.bloemb.BuildConfig;
import com.ancologi.applications.bloemb.Firebase.MyFirebaseMessagingService;
import com.ancologi.applications.bloemb.R;
import com.ancologi.applications.bloemb.Utils.CustomDialog;
import com.ancologi.applications.bloemb.Utils.UserUtils;
import com.ancologi.applications.bloemb.Utils.Utils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.ancologi.applications.bloemb.BuildConfig.DEBUG;


public abstract class MasterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String APP_PREFS = "Ancologi";

    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    int contentViewRes;
    protected Context mContext;
    public static String curr_lang = "ar";
    private DrawerLayout mDrawerLayout;
    protected View mProgressView;
    protected View mFormView;
    protected Snackbar snackbar;
    private CustomDialog customDialog;
    public static Set mUnread_orders ;
    public static Locale locale = new Locale("ar","SA");
    public String app_locale;
    private String channel_id;
    private MediaPlayer mp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("masterB","masterB");
        super.onCreate(savedInstanceState);
        super.setContentView(contentViewRes);
        Log.d("masterA","masterA");
        mContext = this; //assigned in setContentView
        curr_lang = UserUtils.getApplicationLanguage(getApplicationContext());
        if (mFormView != null && mProgressView != null)
            showProgress(false);
        app_locale = readSharedPreferenceString("app_locale");
        Log.d("app_locale",app_locale);
        switch (app_locale){
            case "ar":
                Utils.setAppLocale("ar", MasterActivity.this);
                UserUtils.setApplicationLanguage(getApplicationContext(), "ar");
                deleteSharedPreferenceString("app_locale");
                writeSharedPreferenceString("app_locale","ar");
                break;
            case "en":
                Utils.setAppLocale("en", MasterActivity.this);
                UserUtils.setApplicationLanguage(getApplicationContext(), "en");
                deleteSharedPreferenceString("app_locale");
                writeSharedPreferenceString("app_locale","en");
                break;
            case "ku":
                Utils.setAppLocale("ku", MasterActivity.this);
                UserUtils.setApplicationLanguage(getApplicationContext(), "ku");
                deleteSharedPreferenceString("app_locale");
                writeSharedPreferenceString("app_locale","ku");
                break;
            default:
                Utils.setAppLocale("ar", MasterActivity.this);
                UserUtils.setApplicationLanguage(getApplicationContext(), "ar");
                deleteSharedPreferenceString("app_locale");
                writeSharedPreferenceString("app_locale","ar");
                break;
        }
        if (UserUtils.getApplicationLanguage(getApplicationContext()).equals("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        if (UserUtils.getApplicationLanguage(this).equals("ar")){
            locale = new Locale("ar","SA");
        }else
            locale = new Locale("en","US");


    }

    protected static void setMenuTextColor(final Context context, final Toolbar toolbar, final int menuResId, final int colorRes) {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                View settingsMenuItem =  toolbar.findViewById(menuResId);
                if (settingsMenuItem instanceof TextView) {
                    if (DEBUG) {
                        Log.i("TSAG", "setMenuTextColor textview");
                    }
                    TextView tv = (TextView) settingsMenuItem;
                    tv.setTextColor(ContextCompat.getColor(context, colorRes));
                    tv.setTextSize(5);
                } else { // you can ignore this branch, because usually there is not the situation
//                    Menu menu = toolbar.getMenu();
//                    MenuItem item = menu.findItem(menuResId);
//                    SpannableString s = new SpannableString(item.getTitle());
                    Log.i("TSAN", "setMenuTextColor textview");
//                    s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorRes)), 0, s.length(), 0);
//                    item.setTitle(s);
                }

            }
        });
    }


    protected abstract void getData();

    protected abstract void showData();

    protected abstract void assignUIReferences();

    protected abstract void assignActions();

    //Read from Shared Preferance (INTEGER)
    public int readSharedPreferenceInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    public String readSharedPreferenceString(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    //write shared preferences in integer
    public void writeSharedPreferenceInt(String key , int value) {

        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
    //write shared preferences in String
    public void writeSharedPreferenceString(String key , String value ){

        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public void deleteSharedPreferenceString(String key){
        SharedPreferences sharedPrefere = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        sharedPrefere.edit().remove(key).commit();
    }


    public Set readSharedPreferenceStringSet(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(key,new HashSet<>());
    }

    public void writeSharedPreferenceStringSet(String key , Set value){
        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void  deleteSharedPreferenceStringSet(String key){
        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        sharedPrefereSt.edit().remove(key).commit();
    }

    ///////////////////////////////////////////  read FCM
    //Read from Shared Preferance (INTEGER)
    public int readSharedPreferenceFCMInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyFirebaseMessagingService.APP_PREFS_FCM, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    public String readSharedPreferenceFCMString(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(MyFirebaseMessagingService.APP_PREFS_FCM, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    ///////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.policy){
            Intent policy_intent = new Intent(getBaseContext(), ProfileActivity.class);
            policy_intent.putExtra("company","policy");
            policy_intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(policy_intent);
        }else if (id == R.id.about_us){
            Intent policy_intent = new Intent(getBaseContext(), ProfileActivity.class);
            policy_intent.putExtra("company","about");
            policy_intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(policy_intent);
        }else if (id == R.id.contact_us){
            Intent contact_intent = new Intent(getBaseContext(), ReachUsActivity.class);
            contact_intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(contact_intent);
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void ChangeLanguage(String localeCode)
    {
        writeSharedPreferenceString("app_locale",localeCode);
        String current_local = UserUtils.getApplicationLanguage(getApplicationContext());
        if (!current_local.equals(localeCode)) {
            String msg = "";
            switch (localeCode){
                case "ar":
                    msg = getResources().getString(R.string.confirm_lang_ar);
                    break;
                case "en":
                    msg = getResources().getString(R.string.confirm_lang_en);
                    break;
                case "nl":
                    msg = getResources().getString(R.string.confirm_lang_du);
                    break;
            }
            CustomDialog customDialog = CustomDialog.getInstance(msg,
                    view12 -> Utils.setAppLocale(localeCode, MasterActivity.this), null,
                    this);
            customDialog.setOnConfirmClickListener(view13 -> {
                Utils.setAppLocale(localeCode, MasterActivity.this);
                UserUtils.setApplicationLanguage(getApplicationContext(), localeCode);
                customDialog.closeDialog();
                startActivity(getIntent());
                finish();
            });
            customDialog.showDialog();
        }



    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }



    protected Toolbar set_toolbar(boolean backArrow, String titleId) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (backArrow) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(titleId);

        } else {
            getSupportActionBar().setTitle(titleId);
        }
        return toolbar;
    }

    @Override
    public void setContentView(int viewRes) {
        mContext = this;
        this.contentViewRes = viewRes;
    }

    protected void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.dismiss), view -> {
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title.equals("NA")) title = "";
    }
    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MasterActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public int getScreenHeight() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MasterActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /////////// Call Ws for check Version
    public void CheckVersion()
    {
        JSONObject jsonParam = new JSONObject();

        new SendGetJsonApi(this,"check",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson)
                    {
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);
                            String result = jsonin.optString("result");
                            if (result.equals("success"))
                            {
                                doCheckVersion(jsonin.optString("content"));
                            }
                        }
                        catch ( Exception e) {}
                    }

                    @Override
                    public void onProgress(int process) {
                    }
                }

        ).Execute();

    }

    public void doCheckVersion(String resultVersion)
    {
        int PNumber = 0;
        String google_downloadurl = "";
        String website_downloadurl = "";
        try {
            // Create the root JSONObject from the JSON string.
            JSONObject contentjson = new JSONObject(resultVersion);
            try {
                PNumber = contentjson.optInt("android-version");
            } catch (Exception e) {
            }
            google_downloadurl = contentjson.optString("GoogleUrl");
            website_downloadurl = contentjson.optString("WebSiteUrl");
            Log.d("google_link",google_downloadurl);
            Log.d("google_link",website_downloadurl);
            Log.d("google_link",String.valueOf(PNumber));
            writeSharedPreferenceString("GoogleUrl",google_downloadurl);
            writeSharedPreferenceString("WebSiteUrl",website_downloadurl);

        }
        catch ( Exception e) {}


        if (PNumber > 0 && !website_downloadurl.equals("") && !google_downloadurl.equals(""))
        {
            if (PNumber > versionCode && !MasterActivity.this.isFinishing()) {
                //tp.setText("التطبيق بحاجة لتحديث");
                //  verNum = resws;
                Log.d("google_link","Pnum");

                AlertDialog.Builder dialog = new AlertDialog.Builder(MasterActivity.this);
                dialog.setCancelable(false);
                //dialog.setTitle("يجب تحديث التطبيق");
                TextView title = new TextView(MasterActivity.this);
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

    @Override
    protected void onPause() {
        super.onPause();
        deleteSharedPreferenceStringSet("unread_orders");
        writeSharedPreferenceStringSet("unread_orders",mUnread_orders);
    }






}
