package com.mahta.rastin.broadcastapplication.global;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import io.realm.Realm;

import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.model.UserToken;
import com.mahta.rastin.broadcastapplication.service.NetworkWatcher;

public class G extends Application {
    public static int providerId = 1; //This id is unique for each provider
    public static AppCompatActivity currentActivity;
    public static final String TAG = "mahta";

    //public static final String DOMAIN = "http://192.168.1.33/broadcast_app_server/public";
    public static final String DOMAIN = "https://schoolbroadcastpanel.ir";

    public static final String ABOUT_US_URL = DOMAIN + "/about_us";
    public static final String RULES_URL = DOMAIN + "/rules";
    public static final String POST_URL = DOMAIN + "/api/post/";
    public static final String PROGRAM_URL = DOMAIN + "/api/program/";

    public static final String BASE_URL = DOMAIN + "/api/" + providerId + "/";
    public static final String FILE_URL = DOMAIN + "/uploads/";

    public static final Handler HANDLER = new Handler();
    private BroadcastReceiver networkWatcher;



    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            networkWatcher = new NetworkWatcher();
            registerNetworkBroadcastForNougat();
        }

    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(networkWatcher, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(networkWatcher, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return  connectivityManager != null &&
                connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();

    }

    public static void i(String message){
        Log.i(TAG, message);
    }

    public static void e(String message){
        Log.e(TAG, message);
    }

    public static void toastLong(String message, Context context){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void toastShort(String message, Context context){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static String getStringFromResource(int id, Context context){
        return context.getResources().getString(id);
    }

    public static String[] getStringArrayFromResource(int id, Context context){
        return context.getResources().getStringArray(id);
    }

    public static int getColorFromResource(int id, Context context){
        return context.getResources().getColor(id);
    }

    public static boolean isUserSignedIn(){
        return RealmController.getInstance().hasUserToken();
    }

    public static Bitmap getBitmapFromResources(Resources resources, int resImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = 1;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeResource(resources, resImage, options);
    }

    // TODO: 6/6/18 Remove this method
    private void booz_login(){
        UserToken userToken = new UserToken();
        userToken.setToken("38ccca707cd15ce18bbb3f34197cf777");
        RealmController.getInstance().addUserToken(userToken);
    }
}
