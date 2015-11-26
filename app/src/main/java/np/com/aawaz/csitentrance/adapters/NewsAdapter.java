package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> message = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> imageURL = new ArrayList<>();
    private LayoutInflater inflater;


    public NewsAdapter(Context context, ArrayList<String> message, ArrayList<String> time, ArrayList<String> imageURL) {
        this.context = context;
        this.message = message;
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

        holder.title.setText(generateTitle(message.get(position)));
        holder.newsDetail.setText(generateNews(message.get(position)));
        holder.time.setText(time.get(position));

        if(imageURL.get(position).equals("null"))
            holder.imageView.setVisibility(View.GONE);
        else {
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(imageURL.get(position))
                    .into(holder.imageView);
        }
    }

    private String generateNews(String ss) {
        String[] lines = ss.split(System.getProperty("line.separator"));
        String news="";
        for(int i=2;i<lines.length;i++)
            news=news+"\n"+lines[i];
        return news;
    }

    private String generateTitle(String ss) {
        String[] lines = ss.split(System.getProperty("line.separator"));
        return lines[0];
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView newsDetail;
        TextView time;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.newsTitle);
            newsDetail = (TextView) itemView.findViewById(R.id.newsDetail);
            time = (TextView) itemView.findViewById(R.id.newsTime);
            imageView = (ImageView) itemView.findViewById(R.id.newsImage);
        }
    }
}
