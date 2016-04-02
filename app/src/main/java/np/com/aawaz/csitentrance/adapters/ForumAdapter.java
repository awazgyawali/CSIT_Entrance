package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.MyApplication;


public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> messages, poster, time, image_link;
    ArrayList<Integer> comments;

    ClickListener clickListener;


    public ForumAdapter(Context context, ArrayList<String> poster, ArrayList<String> messages,
                        ArrayList<Integer> comments, ArrayList<String> time, ArrayList<String> image_link) {
        this.context = context;
        this.messages = messages;
        this.poster = poster;
        this.comments = comments;
        this.time = time;
        this.image_link = image_link;
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
        holder.time.setText(time.get(position));
        Picasso.with(MyApplication.getAppContext())
                .load(image_link.get(position))
                .into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addToTop() {
        notifyItemInserted(0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView commentCount, realPost, postedBy, time;
        CircleImageView profile;

        public ViewHolder(View itemView) {
            super(itemView);
            commentCount = (RobotoTextView) itemView.findViewById(R.id.commentCount);
            realPost = (RobotoTextView) itemView.findViewById(R.id.realPost);
            postedBy = (RobotoTextView) itemView.findViewById(R.id.postedBy);
            time = (RobotoTextView) itemView.findViewById(R.id.forumTime);
            profile = (CircleImageView) itemView.findViewById(R.id.forumPic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.itemClicked(view, getAdapterPosition());
                }
            });
        }
    }
}