package com.mahta.rastin.broadcastapplication.service;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        G.i("Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString(Keys.KEY_FIREBASE_TOKEN, refreshedToken).apply();

        String token = preferences.getString(Keys.KEY_TOKEN, "NA");
        if (!token.equals("NA")){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Keys.KEY_TOKEN,token);
            contentValues.put(Keys.KEY_SERVER_FIREBASE_TOKEN,refreshedToken);

            new HttpCommand(HttpCommand.COMMAND_UPDATE_FCM_TOKEN,contentValues)
                    .setOnResultListener(new OnResultListener() {
                        @Override
                        public void onResult(String result) {
                            int resultCode = JSONParser.getResultCodeFromJson(result);
                            if (resultCode == Keys.RESULT_SUCCESS){
                                G.i("token saved");
                            }
                        }
                    }).execute();
        }

    }

}