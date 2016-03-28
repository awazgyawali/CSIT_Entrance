package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.ClickListener;


public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> messages, poster,
            comments, postID;

    ClickListener clickListener;


    public ForumAdapter(Context context, ArrayList<String> poster, ArrayList<String> messages,
                        ArrayList<String> comments, ArrayList<String> postID) {
        this.context = context;
        this.messages = messages;
        this.poster = poster;
        this.comments = comments;
        this.postID = postID;
        inflater = LayoutInflater.from(context);

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.each_feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.commentCount.setText("+" + comments.get(position));
        holder.postedBy.setText(poster.get(position));
        holder.realPost.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView commentCount, realPost, postedBy;
        LinearLayout core;

        public ViewHolder(View itemView) {
            super(itemView);
            commentCount = (RobotoTextView) itemView.findViewById(R.id.commentCount);
            realPost = (RobotoTextView) itemView.findViewById(R.id.realPost);
            postedBy = (RobotoTextView) itemView.findViewById(R.id.postedBy);
            core = (LinearLayout) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.itemClicked(view, getAdapterPosition());
                }
            });
        }
    }
}