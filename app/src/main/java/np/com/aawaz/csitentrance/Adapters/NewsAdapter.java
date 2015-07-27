package np.com.aawaz.csitentrance.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    ArrayList<String> topic = new ArrayList<>();
    ArrayList<String> subTopic = new ArrayList<>();
    ArrayList<String> imageURL = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    ArrayList<String> author = new ArrayList<>();
    LayoutInflater inflater;

    private int expandedPosition = -1;


    public NewsAdapter(Context context, ArrayList<String> topic, ArrayList<String> subTopic, ArrayList<String> imageURL, ArrayList<String> content, ArrayList<String> author) {
        this.context = context;
        this.topic = topic;
        inflater = LayoutInflater.from(context);
        this.subTopic = subTopic;
        this.imageURL = imageURL;
        this.content = content;
        this.author = author;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(topic.get(position));
        holder.themeNews.setText(content.get(position));
        holder.time.setText(subTopic.get(position));
        holder.mainLayout.setMaxCardElevation(10);
        holder.mainLayout.setCardElevation(3);
        holder.titleEach.setText(topic.get(position));
        holder.contentEach.setText(Html.fromHtml(content.get(position)));
        holder.contentEach.setMovementMethod(LinkMovementMethod.getInstance());
        holder.authorEach.setText(author.get(position));
        holder.subTopicEach.setText(subTopic.get(position));
        Picasso.with(context)
                .load(imageURL.get(position))
                .into(holder.newsImage);
        if (position == expandedPosition) {
            holder.llExpandArea.setVisibility(View.VISIBLE);
            holder.mainLayout.setCardElevation(10);
            holder.headings.setVisibility(View.GONE);
        } else {
            holder.headings.setVisibility(View.VISIBLE);
            holder.llExpandArea.setVisibility(View.GONE);
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandedPosition == position) {
                    int pre = expandedPosition;
                    expandedPosition = -1;
                    notifyItemChanged(pre);
                } else if (expandedPosition > -1) {
                    int pre = expandedPosition;
                    expandedPosition = position;
                    notifyItemChanged(pre);
                    notifyItemChanged(expandedPosition);
                } else {
                    expandedPosition = position;
                    notifyItemChanged(expandedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return topic.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView titleEach;
        TextView contentEach;
        TextView authorEach;
        TextView subTopicEach;
        TextView themeNews;
        TextView time;
        ImageView newsImage;
        CardView mainLayout;
        RelativeLayout headings;
        LinearLayout llExpandArea;

        public ViewHolder(View itemView) {
            super(itemView);
            mainLayout = (CardView) itemView.findViewById(R.id.mainLayout);
            titleEach = (TextView) itemView.findViewById(R.id.eachNewsTitle);
            contentEach = (TextView) itemView.findViewById(R.id.newsContent);
            authorEach = (TextView) itemView.findViewById(R.id.newsAuthor);
            subTopicEach = (TextView) itemView.findViewById(R.id.newsSubTitle);
            newsImage = (ImageView) itemView.findViewById(R.id.newsImage);
            title = (TextView) itemView.findViewById(R.id.newsTitle);
            themeNews = (TextView) itemView.findViewById(R.id.themeNews);
            time = (TextView) itemView.findViewById(R.id.time);
            headings = (RelativeLayout) itemView.findViewById(R.id.headings);
            llExpandArea = (LinearLayout) itemView.findViewById(R.id.llExpandArea);
        }
    }
}
