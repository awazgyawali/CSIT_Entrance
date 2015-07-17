package np.com.aawaz.csitentrance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    ArrayList<String> topic=new ArrayList<>();
    ArrayList<String> subTopic=new ArrayList<>();
    ArrayList<String> imageURL=new ArrayList<>();
    ArrayList<String> content=new ArrayList<>();
    ArrayList<String> author=new ArrayList<>();
    LayoutInflater inflater;
    ClickNews clickNews;



    public NewsAdapter(Context context,ArrayList<String> topic,ArrayList<String> subTopic,ArrayList<String> imageURL,ArrayList<String> content,ArrayList<String> author){
        this.context=context;
        this.topic=topic;
        inflater=LayoutInflater.from(context);
        this.subTopic=subTopic;
        this.imageURL=imageURL;
        this.content=content;
        this.author=author;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.news_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(topic.get(position));
        holder.themeNews.setText(content.get(position));
        holder.time.setText(subTopic.get(position));
    }

    @Override
    public int getItemCount() {
        return topic.size();
    }

    public void setClickListner(ClickNews clickNews) {
        this.clickNews = clickNews;

    }

    public interface ClickNews {
        void itemClicked(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView themeNews;
        TextView time;
        public ViewHolder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.newsTitle);
            themeNews= (TextView) itemView.findViewById(R.id.themeNews);
            time= (TextView) itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickNews.itemClicked(view, getAdapterPosition());
                }
            });
        }
    }
}
