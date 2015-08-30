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

    private final View header;
    Context context;
    LayoutInflater inflater;
    ArrayList<String> messages, poster,
            likes, comments, postID;

    ClickListener clickListener;
    int type_header = 0;
    int type_normal = 1;



    public GraphFeedAdapter(Context context, ArrayList<String> poster, ArrayList<String> messages,
                            ArrayList<String> likes, ArrayList<String> comments, ArrayList<String> postID, View header) {
        this.context = context;
        this.messages = messages;
        this.poster = poster;
        this.likes = likes;
        this.comments = comments;
        this.postID = postID;
        this.header = header;
        inflater = LayoutInflater.from(context);

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == type_header)
            return new ViewHolder(header);
        return new ViewHolder(inflater.inflate(R.layout.each_feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0)
            return;
        position--;
        holder.likeCount.setText(likes.get(position));
        holder.commentCount.setText(comments.get(position));
        holder.postedBy.setText(poster.get(position));
        holder.realPost.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return type_header;
        return type_normal;
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
