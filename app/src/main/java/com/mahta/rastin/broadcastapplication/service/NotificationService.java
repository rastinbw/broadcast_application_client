package com.mahta.rastin.broadcastapplication.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.MyNotification;
import com.mahta.rastin.broadcastapplication.model.RequestDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class NotificationService extends Service {
    BackgroundService service;
    RealmManager realmManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!service.isRunning()) {
            service.start();
            service.isRunning = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = new BackgroundService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground();
        }

        G.i("Created");
    }

    private void startForeground() {
        int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NOTIFICATION_CHANNEL_ID = createNotificationChannel();
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification.Builder notificationBuilder = new Notification.Builder(this);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis());
//                .setContentTitle("سرویس اعلان")
//                .setContentInfo("سرویس اعلان برنامه " + G.getStringFromResource(R.string.app_name, getApplicationContext()) + " اجرا شد.");

        startForeground(NOTIFICATION_ID,  notificationBuilder.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        String channelId = "my_service";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (service.isRunning) {
            service.interrupt();
            service.isRunning = false;
            service = null;
        }
    }

    class BackgroundService extends Thread {
        private boolean isRunning = false;
        private long milliSecs = 30000;
        //        Handler networkRequest= new Handler();
        Runnable runTask = new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        G.i("HANDLER");
                        sendRequest();
                        Thread.sleep(milliSecs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isRunning = false;
                    }
                }
            }
        };

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void run() {
            super.run();
            runTask.run();
        }

        private void sendRequest() {
            //getting last request date
            realmManager = new RealmManager();
            String date = realmManager.getRequestDate().getDate();

            //sending request
            new HttpCommand(HttpCommand.COMMAND_GET_LAST_NOTIFICATIONS,null, date)
                    .setOnResultListener(new OnResultListener() {
                        @Override
                        public void onResult(String result) {
                            List<MyNotification> notifications = JSONParser.parseNotifications(result);
                            if (notifications != null) {
                                int i = 0;
                                for (MyNotification notification : notifications) {
                                    showNotification(notification, i++);
                                }
                            }
                        }
                    }).execute();

            //updating request date
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
            date = format.format(now);

            realmManager.clearAllRequestDates();
            realmManager.addRequestDate(new RequestDate(date));

        }
    }

    private void showNotification(MyNotification notification, int i) {
        RemoteViews contentView = new RemoteViews(
                getPackageName(),
                R.layout.layout_notification
        );

        String title = "";
        switch (notification.getCategory_id()){
            case Constant.CATEGORY_ID_POST:
                title = G.getStringFromResource(R.string.new_post,
                        getApplicationContext());
                break;
            case Constant.CATEGORY_ID_MEDIA:
                title = G.getStringFromResource(R.string.new_media,
                        getApplicationContext());
                break;
            case Constant.CATEGORY_ID_PROGRAM:
                title = G.getStringFromResource(R.string.new_program,
                        getApplicationContext());
        }



        contentView.setTextViewText(R.id.text, notification.getContent());
        contentView.setImageViewResource(R.id.image, R.drawable.img_logo);
        contentView.setTextViewText(R.id.title,title);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NOTIFICATION_CHANNEL_ID = createNotificationChannel();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification.Builder notificationBuilder = new Notification.Builder(this);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.img_logo) // notification icon
                .setContent(contentView);

        notificationManager.notify(i, notificationBuilder.build());
    }


    public class RealmManager {
        private Realm realm;

        public RealmManager(){
            realm = Realm.getDefaultInstance();
        }

        public void clearAllRequestDates() {
            realm.beginTransaction();
            RealmResults<RequestDate> result = realm.where(RequestDate.class).findAll();
            result.deleteAllFromRealm();
            realm.commitTransaction();
        }

        public void addRequestDate(RequestDate date){
            realm.beginTransaction();
            realm.copyToRealm(date);
            realm.commitTransaction();
        }

        public RequestDate getRequestDate() {
            if (!realm.where(RequestDate.class).findAll().isEmpty())
                return realm.where(RequestDate.class).findAll().first();
            else {
                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
                return new RequestDate(format.format(now));
            }
        }
    }
}