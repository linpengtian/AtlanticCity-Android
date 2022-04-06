package com.atlanticcity.app.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.atlanticcity.app.Activities.Notifications;
import com.atlanticcity.app.Activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.atlanticcity.app.Activities.DealsFullScreen;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.Utilities;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Utilities utils = new Utilities();


    IntentFilter intentFilter;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {
            utils.print(TAG, "From: " + remoteMessage.getFrom());
            utils.print(TAG, "Notification Message Body: " + remoteMessage.getData());
            //Calling method to generate notification
            sendNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"),remoteMessage.getData().get("user_id"),remoteMessage.getData().get("item_id"));
        }else{
            utils.print(TAG,"FCM Notification failed");
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String title, String body, String user_id, String item_id) {
   /*     Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification",messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);*/

        Intent resultIntent = new Intent(this, Notifications.class);
        resultIntent.putExtra("title",title);
        resultIntent.putExtra("body",body);
        resultIntent.putExtra("user_id",user_id);
        resultIntent.putExtra("item_id",item_id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent);


        /*registerReceiver(mMessageReceiver,new IntentFilter("com.atlanticcity.app.CUSTOM_INTENT"));
        Intent intent=new Intent("com.atlanticcity.app.CUSTOM_INTENT");

        sendBroadcast(intent);*/

        //  notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getApplicationContext().getString(R.string.
                    default_notification_channel_id);
            NotificationChannel channel = new NotificationChannel(channelId, "ATLANTICCITY", NotificationManager.
                    IMPORTANCE_DEFAULT);
            channel.setDescription(title);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            return R.drawable.logo;
        }else {
            return R.mipmap.ic_launcher;
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getAction();
            Intent i = new Intent(context,Notifications.class);
            i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    };

}


