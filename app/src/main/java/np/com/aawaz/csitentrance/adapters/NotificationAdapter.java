package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import np.com.aawaz.csitentrance.R;
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
        return new VH(LayoutInflater.from(context).inflate(R.layout.each_notification_view,parent,false));
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.VH holder, int position) {
        Toast.makeText(context, notificationArrayList.get(position).text, Toast.LENGTH_SHORT).show();
        Notification notification=notificationArrayList.get(holder.getAdapterPosition());

        holder.title.setText(notification.text);
        holder.text.setText(notification.title);
        holder.tag.setText(notification.tag);
        holder.time.setText(getRelativeTime( notification.time));
    }

    private String getRelativeTime(long time) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(time, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

     class VH extends RecyclerView.ViewHolder {
        TextView title,text,tag,time;
         VH(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.notificationTitle);
            text= (TextView) itemView.findViewById(R.id.notificationMessage);
            tag = (TextView) itemView.findViewById(R.id.notificationTag);
            time= (TextView) itemView.findViewById(R.id.notificationTime);
        }
    }
}
