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

import java.util.ArrayList;
import java.util.Random;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.activities.NewsDetailActivity;
import np.com.aawaz.csitentrance.objects.Notification;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class MyMessagingService extends FirebaseMessagingService {
    public MyMessagingService() {
    }

    public static void sendNotification(Context context, Notification notification) {
        Intent intent;
        int identifier = new Random().nextInt();

        switch (notification.tag) {
            case "news":
                identifier = "news".hashCode();
                intent = new Intent(context, NewsDetailActivity.class)
                        .putExtra("news_id", notification.post_id);
                break;
            case "forum":
                if (notification.text.contains("commented"))
                    identifier = notification.post_id.hashCode();
                else if (notification.text.contains("posted")) {
                    identifier = "posted".hashCode();
                    SPHandler.getInstance().addNewPostMessage(notification.title);
                    notification.post_id = "new_post";
                }
                intent = new Intent(context, MainActivity.class).putExtra("fragment", notification.tag);
                intent.putExtra("post_id", notification.post_id);
                notification.addToDatabase();
                break;
            default:
                intent = new Intent(context, MainActivity.class)
                        .putExtra("fragment", notification.tag)
                        .putExtra("result_published", notification.result_published);
                break;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, identifier, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.splash_icon))
                .setSmallIcon(R.drawable.skeleton_logo)
                .setContentText(Html.fromHtml(notification.text).toString())
                .setContentTitle(notification.title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        int count = SPHandler.getInstance().getUnreadPostCount();
        if (notification.text.contains("posted") && count > 1) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            ArrayList<String> messages = SPHandler.getInstance().getUnreadPostMessages();
            for (int i = count; i > 0; i--) {
                inboxStyle.addLine(messages.get(i - 1));
            }
            inboxStyle.setSummaryText(count + " new posts on Entrance Forum");
            notificationBuilder.setStyle(inboxStyle);
            notificationBuilder.setContentTitle("Entrance Forum");
            notificationBuilder.setContentText(count + " new posts on Entrance Forum");
        }

        if (notification.tag.equals("news")) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(notification.text)));
            FirebaseDatabase.getInstance().getReference().child("news").keepSynced(true);
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notification.tag.equals("forum")) {
            FirebaseDatabase.getInstance().getReference().child("forum").keepSynced(true);
            if (!notification.uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                notificationManager.notify(identifier, notificationBuilder.build());
        } else
            notificationManager.notify(identifier, notificationBuilder.build());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;


        Notification notification = new Notification();
        notification.title = remoteMessage.getData().get("title");
        notification.text = remoteMessage.getData().get("body");
        notification.tag = remoteMessage.getData().get("fragment");
        notification.result_published = Boolean.parseBoolean(remoteMessage.getData().get("result_published"));
        if (notification.tag.equals("forum")) {
            notification.post_id = remoteMessage.getData().get("post_id");
            notification.uid = remoteMessage.getData().get("uid");
        } else if (notification.tag.equals("news"))
            notification.post_id = remoteMessage.getData().get("news_id");

        sendNotification(this, notification);
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