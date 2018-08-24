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

        switch (notification.getTag()) {
            case "news":
                identifier = "news".hashCode();
                intent = new Intent(context, NewsDetailActivity.class)
                        .putExtra("news_id", notification.getPost_id());
                break;
            case "forum":
                if (notification.getText().contains("commented"))
                    identifier = notification.getPost_id().hashCode();
                else if (notification.getText().contains("posted")) {
                    identifier = "posted".hashCode();
                    if (!notification.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        SPHandler.getInstance().addNewPostMessage(notification.getTitle());
                    notification.setPost_id("new_post");
                }
                intent = new Intent(context, MainActivity.class).putExtra("fragment", notification.getTag());
                intent.putExtra("post_id", notification.getPost_id());
                if (!notification.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    notification.addToDatabase();
                break;
            case "discussion":
                if (notification.getText().contains("commented"))
                    identifier = notification.getPost_id().hashCode();
                else if (notification.getText().contains("opened")) {
                    identifier = "opened".hashCode();
                    if (!notification.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        SPHandler.getInstance().addNewDiscussionMessage(notification.getTitle());
                    notification.setPost_id("new_post");
                }
                intent = new Intent(context, MainActivity.class).putExtra("fragment", notification.getTag());
                intent.putExtra("discussion_id", notification.getPost_id());
                if (!notification.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    notification.addToDatabase();
                break;


            default:
                intent = new Intent(context, MainActivity.class)
                        .putExtra("fragment", notification.getTag())
                        .putExtra("result_published", notification.getResult_published());
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
                .setContentText(Html.fromHtml(notification.getText()).toString())
                .setContentTitle(notification.getTitle())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        int post_count = SPHandler.getInstance().getUnreadPostCount();
        int discussion_count = SPHandler.getInstance().getUnreadDiscussionCount();
        if (notification.getText().contains("posted") && post_count > 1) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            ArrayList<String> messages = SPHandler.getInstance().getUnreadPostMessages();
            for (int i = post_count; i > 0; i--) {
                inboxStyle.addLine(messages.get(i - 1));
            }
            inboxStyle.setSummaryText(post_count + " new posts on Entrance Forum");
            notificationBuilder.setStyle(inboxStyle);
            notificationBuilder.setContentTitle("Entrance Forum");
            notificationBuilder.setContentText(post_count + " new posts on Entrance Forum");
        }

        if (notification.getText().contains("opened") && discussion_count > 1) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            ArrayList<String> messages = SPHandler.getInstance().getUnreadDiscussionMessages();
            for (int i = discussion_count; i > 0; i--) {
                inboxStyle.addLine(messages.get(i - 1));
            }
            inboxStyle.setSummaryText(discussion_count + " new discussions opened.");
            notificationBuilder.setStyle(inboxStyle);
            notificationBuilder.setContentTitle("Question Discussion");
            notificationBuilder.setContentText(discussion_count + " new discussions opened.");
        }

        if (notification.getTag().equals("news")) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(notification.getText())));
            FirebaseDatabase.getInstance().getReference().child("news").keepSynced(true);
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notification.getTag().equals("forum") || notification.getTag().equals("discussion")) {
            if (!notification.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                notificationManager.notify(identifier, notificationBuilder.build());
        } else
            notificationManager.notify(identifier, notificationBuilder.build());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        Notification notification = new Notification();
        notification.setTitle(remoteMessage.getData().get("title"));
        notification.setText(remoteMessage.getData().get("body"));
        notification.setTag(remoteMessage.getData().get("fragment"));
        notification.setResult_published(Boolean.parseBoolean(remoteMessage.getData().get("result_published")));
        if (notification.getTag().equals("forum")) {
            notification.setPost_id(remoteMessage.getData().get("post_id"));
            notification.setUid(remoteMessage.getData().get("uid"));
        } else if(notification.getTag().equals("discussion")){
            notification.setPost_id(remoteMessage.getData().get("post_id"));
            notification.setUid(remoteMessage.getData().get("uid"));
        }else if (notification.getTag().equals("news"))
            notification.setPost_id(remoteMessage.getData().get("news_id"));

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