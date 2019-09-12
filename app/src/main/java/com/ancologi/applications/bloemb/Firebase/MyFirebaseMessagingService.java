package com.ancologi.applications.bloemb.Firebase;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.R;
import com.google.firebase.BuildConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.ancologi.applications.bloemb.Masters.MasterActivity.APP_PREFS;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onCreate() {
    }

    private static final String TAG = "MyFirebaseMsgService";

    private final static AtomicInteger c = new AtomicInteger(0);

    public static final String APP_PREFS_FCM = "Like_FCM";

    public int UserId=0;
    public String Token = "",FCM_Token="";
    Set unread_records = new HashSet<>();
    public int budges_count=0;


    /**
     * Called when message is received.
     *
     * param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        //Log.d("NEW_TOKEN",s);
        if (BuildConfig.BUILD_TYPE.contains("demo"))
        {
            FirebaseMessaging.getInstance().subscribeToTopic("all-users-demo");
        }
        else
        {
            FirebaseMessaging.getInstance().subscribeToTopic("all-users");
            Log.d("newToken_2","topic");
        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // reset token\


        writeSharedPreferenceFCMInt("TokenSent",0);

        if (refreshedToken != null && !refreshedToken.equals("")) {
            // save new token
            writeSharedPreferenceFCMString("FCM_Token", refreshedToken);
            // send to server
            sendRegistrationToServer(refreshedToken);
        }
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("Token");

        FCM_Token= token;

        if(UserId > 0 && !Token.equals(""))
        {
            new MyFirebaseSendTokenToServer(this,UserId,Token,FCM_Token).Execute();
        }


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // sender id 265903541033

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            /*if ( Check if data needs to be processed by long running job  true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
                handleNow();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }*/

            unread_records = readSharedPreferenceStringSet("unread_records");
            budges_count = readSharedPreferenceInt("budges_count");
            budges_count ++;

            writeSharedPreferenceInt("budges_count",budges_count);
            String new_record;
            String action = remoteMessage.getData().get("action");
            Log.d("unread_records0",String.valueOf(action));
            Log.d("unread_records",String.valueOf(unread_records));
            if (action != null)
                switch (action){
                    case "custom":
                        new_record = remoteMessage.getData().get("record_id");
                        Log.d("unread_records0",String.valueOf(new_record));
                        unread_records.add(new_record);
                        Log.d("unread_records1",String.valueOf(unread_records));
                        break;
                    case "stacked":
                        new_record = remoteMessage.getData().get("record_id");
                        Log.d("unread_records0",String.valueOf(new_record));
                        String replace = new_record.replace("[","");
                        String replace1 = replace.replace("]","");
                        List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
                        unread_records.addAll(myList);
                        Log.d("unread_records2",String.valueOf(unread_records));
                        break;
                        default:
                            break;
                }




            sendNotification(remoteMessage.getData().get("body"),remoteMessage.getData().get("title"),remoteMessage.getData().get("bodyAr"), remoteMessage.getData().get("action"),remoteMessage.getData().get("record_id"));

            ShortcutBadger.applyCount(getApplicationContext(), budges_count); //for 1.1.4+
//
//            try {
//                Badges.setBadge(getApplicationContext(), budges_count);
//            } catch (BadgesNotSupportedException badgesNotSupportedException) {
//                Log.d(TAG, badgesNotSupportedException.getMessage());
//            }
        }



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle(),null,"","");
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    /*private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }*/




    //Read from Shared Preferance (INTEGER)
    private int readSharedPreferenceInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    private String readSharedPreferenceString(String key){
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


    //write shared preferences in integer
    private void writeSharedPreferenceFCMInt(String key , int value) {

        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS_FCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
    //write shared preferences in String
    private void writeSharedPreferenceFCMString(String key , String value ){

        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS_FCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();

        editor.putString(key, value);
        editor.commit();
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








    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody,String title ,String messageBodyAr, String action,String record_id) {

        if (messageBody==null) messageBody="";
        if (messageBodyAr==null) messageBodyAr = messageBody;


        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS + "_lang", Context.MODE_PRIVATE);
        String lang =  sharedPreferences.getString("lang","");
        String sys_lang = Locale.getDefault().getLanguage();
        if (lang.equals("ar") || sys_lang.equals("ar")) messageBody = messageBodyAr;


        if (action==null) action="";
        if (record_id == null) record_id = "";

        Intent intent;
        if (action.equals("all" ))
        {
            Log.d("unread_records3","entered_all");
            intent = new Intent(this, MasterActivity.class); // home activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("FCM_ACTION", "show_my_view");
        }
        else if (action.equals("custom"))
        {
            Log.d("unread_records3","entered_custom");
            intent = new Intent(this, MasterActivity.class); // show activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("FCM_ACTION", "custom");
            intent.putExtra("record_id",Integer.parseInt(record_id));

        }else
        {
            intent = new Intent(this, MasterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        int notification_sound = readSharedPreferenceInt("Notification_sound");
        Log.d("notification_sounddd",String.valueOf(notification_sound));

        boolean isMuted = false;
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID  = "fcm_default_channel" + notification_sound;


        //String channelId = getString(R.string.default_notification_channel_id);

//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent m_intent = new Intent("MyData");
        m_intent.putExtra("CHANNEL_ID", CHANNEL_ID);






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

            for (int i = 0; channelList != null && i < channelList.size(); i++) {
                notificationManager.deleteNotificationChannel(channelList.get(i).getId());
            }


            Log.d("SDK_INT",String.valueOf(Build.VERSION.SDK_INT));
            CharSequence name = "naawa" + notification_sound;
            String Description = "This is my channel";
            int importance;
            if (isMuted){
                importance = NotificationManager.IMPORTANCE_LOW;
            }else
            {
                importance = NotificationManager.IMPORTANCE_HIGH;
            }
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(defaultSoundUri,attributes);
            notificationManager.createNotificationChannel(mChannel);


            Log.d("yesy",String.valueOf(defaultSoundUri));
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_raw)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setNumber(budges_count)
                        .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(c.incrementAndGet() /* ID of notification */, notificationBuilder.build());

    }




}