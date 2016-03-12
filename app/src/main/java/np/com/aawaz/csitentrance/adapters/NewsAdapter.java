package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import np.com.aawaz.csitentrance.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final ArrayList<String> title,
            message,
            time,
            imageURL;
    private LayoutInflater inflater;
    private Context context;


    public NewsAdapter(Context context, ArrayList<String> title, ArrayList<String> message, ArrayList<String> time, ArrayList<String> imageURL) {
        this.context = context;
        this.message = message;
        this.title = title;
        inflater = LayoutInflater.from(context);
        this.time = time;
        this.imageURL = imageURL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.title.setText(title.get(position));
        holder.newsDetail.setText(Html.fromHtml(message.get(position)));
        holder.time.setText(convertToSimpleDate(time.get(position)));

        if (imageURL.get(position).equals("null"))
            holder.imageView.setVisibility(View.GONE);
        else {
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(imageURL.get(position))
                    .into(holder.imageView);
        }
    }

    private CharSequence convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = simpleDateFormat.parse(created_time);
            return DateUtils.getRelativeDateTimeString(context, date.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_SHOW_TIME);
        } catch (ParseException ignored) {
        }
        return "Unknown Time";
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView title, newsDetail, time;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (RobotoTextView) itemView.findViewById(R.id.newsTitle);
            newsDetail = (RobotoTextView) itemView.findViewById(R.id.newsDetail);
            time = (RobotoTextView) itemView.findViewById(R.id.newsTime);
            imageView = (ImageView) itemView.findViewById(R.id.newsImage);
        }
    }
}
