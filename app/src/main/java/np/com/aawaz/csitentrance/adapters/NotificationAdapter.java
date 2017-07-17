package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.activities.NewsDetailActivity;
import np.com.aawaz.csitentrance.objects.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {

    private final Context context;
    private final ArrayList<Notification> notificationArrayList;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationArrayList) {

        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @Override
    public NotificationAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.each_notification_view, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.VH holder, int position) {
        final Notification notification = notificationArrayList.get(holder.getAdapterPosition());

        holder.title.setText(notification.text);
        holder.text.setText(notification.title);
        holder.tag.setText(notification.tag);
        holder.time.setText(getRelativeTime(notification.time));
        holder.core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.tag.equals("FORUM")) {
                    if (notification.post_id == null)
                        context.startActivity(new Intent(context, MainActivity.class)
                                .putExtra("fragment", "forum")
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    else
                        context.startActivity(new Intent(context, MainActivity.class)
                                .putExtra("post_id", notification.post_id)
                                .putExtra("fragment", "forum")
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

                    ((AppCompatActivity) context).finish();
                } else if (notification.tag.equals("NEWS")) {
                    context.startActivity(new Intent(context, NewsDetailActivity.class)
                            .putExtra("news_id", notification.post_id));
                    ((AppCompatActivity) context).finish();
                }
            }
        });
    }

    private String getRelativeTime(long time) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(time, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title, text, tag, time;
        View core;

        VH(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.notificationTitle);
            text = (TextView) itemView.findViewById(R.id.notificationMessage);
            tag = (TextView) itemView.findViewById(R.id.notificationTag);
            time = (TextView) itemView.findViewById(R.id.notificationTime);
            core = itemView;
        }
    }
}
