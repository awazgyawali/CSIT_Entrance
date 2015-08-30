package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;


public class GraphFeedAdapter extends RecyclerView.Adapter<GraphFeedAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> messages, poster,
            likes, comments, postID;

    ClickListener clickListener;


    public GraphFeedAdapter(Context context, ArrayList<String> poster, ArrayList<String> messages,
                            ArrayList<String> likes, ArrayList<String> comments, ArrayList<String> postID) {
        this.context = context;
        this.messages = messages;
        this.poster = poster;
        this.likes = likes;
        this.comments = comments;
        this.postID = postID;
        inflater = LayoutInflater.from(context);

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.each_feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.likeCount.setText(likes.get(position));
        holder.commentCount.setText(comments.get(position));
        holder.postedBy.setText(poster.get(position));
        holder.realPost.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface ClickListener {
        void itemClicked(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentCount;
        TextView likeCount;
        TextView realPost;
        TextView postedBy;

        public ViewHolder(View itemView) {
            super(itemView);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            realPost = (TextView) itemView.findViewById(R.id.realPost);
            postedBy = (TextView) itemView.findViewById(R.id.postedBy);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.itemClicked(view, getAdapterPosition());
                }
            });
        }
    }


}
