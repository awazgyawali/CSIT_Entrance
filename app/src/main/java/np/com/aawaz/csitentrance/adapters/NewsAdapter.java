package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
        holder.title.setText(news.get(holder.getAdapterPosition()).title);
        holder.newsDetail.setText(Html.escapeHtml(news.get(holder.getAdapterPosition()).message));
        holder.time.setText(convertToSimpleDate(news.get(holder.getAdapterPosition()).time_stamp));

        holder.core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", news.get(holder.getAdapterPosition()).title);
                bundle.putString("author", news.get(holder.getAdapterPosition()).author);
                bundle.putString("detail", news.get(holder.getAdapterPosition()).message);
                bundle.putLong("time", news.get(holder.getAdapterPosition()).time_stamp);
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
