package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    ArrayList<String> topic = new ArrayList<>();
    ArrayList<String> subTopic = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    LayoutInflater inflater;
    ClickListener clickListener;


    public NewsAdapter(Context context, ArrayList<String> topic, ArrayList<String> subTopic, ArrayList<String> content) {
        this.context = context;
        this.topic = topic;
        inflater = LayoutInflater.from(context);
        this.subTopic = subTopic;
        this.content = content;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.news_item, parent, false));
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position % 2 == 0)
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500)
                    .playOn(holder.headings);
        else
            YoYo.with(Techniques.FadeInRight)
                    .duration(500)
                    .playOn(holder.headings);
        holder.title.setText(topic.get(position));
        holder.themeNews.setText(content.get(position));
        holder.time.setText(subTopic.get(position));
    }

    @Override
    public int getItemCount() {
        return topic.size();
    }

    public interface ClickListener {
        void itemClicked(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView themeNews;
        TextView time;
        RelativeLayout headings;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.newsTitle);
            themeNews = (TextView) itemView.findViewById(R.id.themeNews);
            time = (TextView) itemView.findViewById(R.id.newsTime);
            headings = (RelativeLayout) itemView.findViewById(R.id.headings);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.itemClicked(view, getAdapterPosition());
                }
            });
        }
    }
}
