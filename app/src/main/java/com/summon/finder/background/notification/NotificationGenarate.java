package com.summon.finder.background.notification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.summon.finder.R;

import java.util.Random;

public class NotificationGenarate extends BroadcastReceiver {
    public static final String NOTIFY_SEEN = "com.summon.finder.seen";
    private static final int NOTIFICATION_ID_CUSTOM_BIG = 9;

    private final Context context;


    public NotificationGenarate(Context context) {
        this.context = context;
    }

    private static void setListeners(RemoteViews view, Context context) {
        Intent cancel = new Intent(NOTIFY_SEEN);


        PendingIntent pSeen = PendingIntent.getBroadcast(context, 0, cancel, PendingIntent.FLAG_UPDATE_CURRENT);
//        view.setOnClickPendingIntent(R.id.btn_cancel, pSeen);

    }

    public void customBigNotification() {
        String id = "my_channel_id_01";
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(id);
            if (channel == null) {
                channel = new NotificationChannel(id, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
                //config nofication channel
                channel.setDescription("[Channel description]");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.view_notify_message);

        Intent notificationIntent = new Intent(context, ActivityCompat.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.drawable.ic_icon_primary)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon_primary))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.sample1))
                        .bigLargeIcon(null))
                .setContentTitle("Title")
                .setContentText("Your text description")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setAutoCancel(false)//true touch on notificaiton menu dismissed, but swipe to dismiss
                .setTicker("Nofiication");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m = NotificationManagerCompat.from(context);
        //id to generate new notification in list notifications menu
        m.notify(new Random().nextInt(), builder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
