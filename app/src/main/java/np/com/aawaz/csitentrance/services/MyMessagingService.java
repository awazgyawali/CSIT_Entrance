package np.com.aawaz.csitentrance.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.activities.NewsDetailActivity;
import np.com.aawaz.csitentrance.objects.Notification;

public class MyMessagingService extends FirebaseMessagingService {
    public MyMessagingService() {
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Notification notification = new Notification();

        Intent intent;

        notification.title = remoteMessage.getData().get("title");
        notification.text = remoteMessage.getData().get("body");
        notification.post_id = "";
        notification.tag = "";

        if (remoteMessage.getData().get("fragment").equals("news")) {
            intent = new Intent(this, NewsDetailActivity.class)
                    .putExtra("news_id", remoteMessage.getData().get("news_id"));
            notification.post_id = remoteMessage.getData().get("news_id");
            notification.tag = "NEWS";
        } else
            intent = new Intent(this, MainActivity.class)
                    .putExtra("fragment", remoteMessage.getData().get("fragment"))
                    .putExtra("result_published", remoteMessage.getData().get("result_published"));
        if (remoteMessage.getData().get("fragment").equals("forum")) {
            notification.post_id = remoteMessage.getData().get("post_id");
            notification.tag = "FORUM";
            intent.putExtra("post_id", remoteMessage.getData().get("post_id"));
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(100), intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.splash_icon))
                .setSmallIcon(R.drawable.skeleton_logo)
                .setContentText(Html.fromHtml(remoteMessage.getData().get("body")).toString())
                .setContentTitle(remoteMessage.getData().get("title"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (remoteMessage.getData().get("fragment").equals("news")) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(remoteMessage.getData().get("body"))));
            FirebaseDatabase.getInstance().getReference().child("news").keepSynced(true);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (remoteMessage.getData().get("fragment").equals("forum")) {
            FirebaseDatabase.getInstance().getReference().child("forum").keepSynced(true);
            if (!remoteMessage.getData().get("uid").equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        } else
            notificationManager.notify(new Random().nextInt() /* ID of notification */, notificationBuilder.build());
        notification.addToDatabase();
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        e.printStackTrace();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }
}