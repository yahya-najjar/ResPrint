package com.ancologi.applications.bloemb.Api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.ancologi.applications.bloemb.BuildConfig;
import com.ancologi.applications.bloemb.Masters.MasterActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ya7ya on 11/3/2018.
 */

public class SendGetJsonApi  {

    private String ApiUrl;
    private JSONObject JsonSend;
    private  String token;

    private int ConnectTimeout = 15000;
    private int ReadTimeout = 30000;


    private Map<String[],InputStream> Files = new HashMap<String[], InputStream>();

    private boolean isUploadFiles = false;

    private CallBackListener callBackListener;

    public boolean isCanceled = false;

    private HttpURLConnection urlConnection;

    private  Context context;


    public static String WebSite()
    {
        if (BuildConfig.BUILD_TYPE.contains("demo"))
        {
            return "http://192.168.170.20:8000/api";
        }
        else
        {
//            return  "http://192.168.170.32:8000/api/";
//            return  "http://10.0.0.14:8000/api/";
//            return "http://185.158.92.37/wafyat-api/public/api/";
//            return  "http://192.168.170.19:8000/v1.1/api/";
            return "http://yahya.ancologi.nl/Restaurant/index.php/api/";
        }
    }


    public static String BaseURL(){
        return "http://yahya.ancologi.nl/Restaurant/index.php/";
    }



    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend,CallBackListener callBackListener) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;
        this.callBackListener = callBackListener;
    }

    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend,String token,CallBackListener callBackListener) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;
        this.callBackListener = callBackListener;
        this.token = token;
    }

    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend ,int connectTimeout , int readTimeout,CallBackListener callBackListener ) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;

        this.ConnectTimeout = connectTimeout;
        this.ReadTimeout = readTimeout;
        this.callBackListener = callBackListener;
    }

    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend, Map<String[],InputStream> files, CallBackListener callBackListener ) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;
        this.Files = files;
        this.isUploadFiles = true;
        this.callBackListener = callBackListener;
    }


    public void Execute()
    {
        asyncTask atask = new asyncTask();
        atask.execute();
    }


    private class asyncTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            StringBuilder resultjosn = new StringBuilder();
            String link = WebSite() + ApiUrl;
            try {
                URL url = new URL(link);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");



                urlConnection.setRequestProperty("X-localization", MasterActivity.curr_lang);
//                urlConnection.setRequestProperty("Authorization","Bearer " + token );

                if (!isUploadFiles) {
                    urlConnection.setConnectTimeout(ConnectTimeout);
                    urlConnection.setReadTimeout(ReadTimeout);

                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");

                    urlConnection.connect(); // Note the connect() here


                    OutputStream os = urlConnection.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    osw.write(JsonSend.toString());

                    osw.flush();
                    osw.close();

                } else {
                    uploadFilesAndData();
                }


                InputStream in;

                int status = urlConnection.getResponseCode();

                if (status == 200) {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } else {
                    in = urlConnection.getErrorStream();
                }


                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    resultjosn.append(line);
                }


            } catch (Exception e) {
                return new String("{'result':'error','error_des':'" + e.getMessage() + "'}");
            } finally {
                urlConnection.disconnect();
            }
            return resultjosn.toString();

        }


        private void uploadFilesAndData() throws Exception {

        }

        @Override
        protected void onPostExecute(String resultjson)
        {
            if ( context instanceof Activity) {
                Activity activity = (Activity)context;
                if ( activity.isFinishing() ) {
                    return;
                }
            }
            callBackListener.onFinish(resultjson);

        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            if ( context instanceof Activity) {
                Activity activity = (Activity)context;
                if ( activity.isFinishing() ) {
                    return;
                }
            }

            callBackListener.onProgress(values[0]);

        }
    }

}
