package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.EachNews;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.objects.News;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> news = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private ClickListener listener;


    public NewsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addToTop(News new_news) {
        news.add(0, new_news);
        notifyItemInserted(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.each_news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.title.setText(news.get(position).title);
        holder.newsDetail.setText(Html.fromHtml(news.get(position).message));
        holder.time.setText(convertToSimpleDate(news.get(position).time_stamp));

        if (news.get(position).image_url.equals("null"))
            holder.imageView.setVisibility(View.GONE);
        else {
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(news.get(position).image_url)
                    .into(holder.imageView);
        }
        holder.core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", news.get(position).title);
                bundle.putString("detail", news.get(position).message);
                bundle.putLong("time", news.get(position).time_stamp);
                bundle.putString("image_link", news.get(position).image_url);

                if (news.get(position).image_url.equals("null"))
                    context.startActivity(new Intent(context, EachNews.class).putExtra("data", bundle));
                else {
                    if (Build.VERSION.SDK_INT >= 21)
                        context.startActivity(new Intent(context, EachNews.class).putExtra("data",bundle),
                                ActivityOptionsCompat.makeSceneTransitionAnimation((AppCompatActivity) context, holder.imageView, "destinationNews").toBundle());
                    else
                        context.startActivity(new Intent(context, EachNews.class).putExtra("data",bundle));

                }
            }
        });
    }

    private CharSequence convertToSimpleDate(long created_time) {
        return DateUtils.getRelativeTimeSpanString(created_time, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView title, newsDetail, time;
        ImageView imageView;
        View core;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = (RobotoTextView) itemView.findViewById(R.id.newsTitle);
            newsDetail = (RobotoTextView) itemView.findViewById(R.id.newsDetail);
            time = (RobotoTextView) itemView.findViewById(R.id.newsTime);
            imageView = (ImageView) itemView.findViewById(R.id.newsImage);
            core = itemView;
        }
    }
}
