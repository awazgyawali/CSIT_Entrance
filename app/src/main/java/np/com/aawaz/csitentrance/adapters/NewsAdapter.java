package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.NewsDetailActivity;
import np.com.aawaz.csitentrance.objects.News;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> news = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;


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
        final News eachNews = news.get(position);
        holder.title.setText(eachNews.title);
        holder.newsDetail.setText(eachNews.excerpt);
        holder.time.setText(eachNews.author + " - " + convertToSimpleDate(eachNews.time_stamp));

        holder.core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", eachNews.title);
                bundle.putString("author", eachNews.author);
                bundle.putString("detail", eachNews.message);
                bundle.putLong("time", eachNews.time_stamp);
                context.startActivity(new Intent(context, NewsDetailActivity.class).putExtra("data", bundle));
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

    public News getNewsAt(int position) {
        return news.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, newsDetail, time;
        View core;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.newsTitle);
            newsDetail = (TextView) itemView.findViewById(R.id.newsDetail);
            time = (TextView) itemView.findViewById(R.id.newsTime);
            core = itemView;
        }
    }
}
