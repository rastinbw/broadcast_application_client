package com.mahta.rastin.broadcastapplication.global;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import io.realm.Realm;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Slider;
import com.mahta.rastin.broadcastapplication.model.Staff;
import com.mahta.rastin.broadcastapplication.model.StaffUpdated;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.mahta.rastin.broadcastapplication.model.UserToken;
import com.mahta.rastin.broadcastapplication.service.NetworkWatcher;

import java.util.List;

public class G extends Application {
    public static int providerId = 1; //This id is unique for each provider
    public static AppCompatActivity currentActivity;
    public static final String TAG = "mahta";

    public static final String DOMAIN = "http://192.168.1.35/broadcast_app_server/public";
    // public static final String DOMAIN = "https://schoolbroadcastpanel.ir";

    public static final String ABOUT_US_URL = DOMAIN + "/about_us";
    public static final String RULES_URL = DOMAIN + "/rules";
    public static final String POST_URL = DOMAIN + "/api/post/";
    public static final String PROGRAM_URL = DOMAIN + "/api/program/";

    public static final String BASE_URL = DOMAIN + "/api/" + providerId + "/";
    public static final String FILE_URL = DOMAIN + "/storage/";

    public static final Handler HANDLER = new Handler();
    private BroadcastReceiver networkWatcher;

    public static Slider slider = null;
    public static RealmController realmController;


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        realmController = new RealmController(getApplicationContext());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            networkWatcher = new NetworkWatcher();
            registerNetworkBroadcastForNougat();
        }

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        return realmController.hasUserToken();
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

}
