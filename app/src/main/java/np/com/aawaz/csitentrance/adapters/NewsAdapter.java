package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
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
import np.com.aawaz.csitentrance.fragments.other_fragments.EachNews;
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

    public void removeItemAtPosition(int i) {
        news.remove(i);
        notifyItemRemoved(i);
    }

    public void editItemAtPosition(int i, News new_news) {
        news.remove(i);
        news.add(i, new_news);
        notifyItemChanged(i);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(news.get(position).title);
        holder.newsDetail.setText(Html.fromHtml(news.get(position).message));
        holder.time.setText(convertToSimpleDate(news.get(position).time_stamp));

        if (news.get(position).image_url.equals(""))
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
                BottomSheetDialogFragment bottomSheetDialogFragment = new EachNews();

                Bundle bundle = new Bundle();
                bundle.putString("title", news.get(position).title);
                bundle.putString("detail", news.get(position).message);
                bundle.putLong("time", news.get(position).time_stamp);
                bundle.putString("image_link", news.get(position).image_url);

                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

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
