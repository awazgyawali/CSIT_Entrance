package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.objects.Post;


public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<Post> posts = new ArrayList<>();

    ClickListener clickListener;


    public ForumAdapter(Context context) {
        this.context = context;
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

        holder.postedBy.setText(posts.get(position).author);
        holder.realPost.setText(posts.get(position).message);
        holder.commentCount.setText(posts.get(position).comment_count + " comments");
        holder.time.setText(DateUtils.getRelativeTimeSpanString(posts.get(position).time_stamp, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(posts.get(position).uid))
            holder.postedBy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_grey, 0);
        else
            holder.postedBy.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        try {
            Picasso.with(MyApplication.getAppContext())
                    .load(posts.get(position).image_url)
                    .error(TextDrawable.builder().buildRound(String.valueOf(posts.get(holder.getAdapterPosition()).author.charAt(0)).toUpperCase(), Color.BLUE))
                    .placeholder(TextDrawable.builder().buildRound(String.valueOf(posts.get(holder.getAdapterPosition()).author.charAt(0)).toUpperCase(), Color.BLUE))
                    .into(holder.profile);
        } catch (IllegalArgumentException e) {
            holder.profile.setImageDrawable(TextDrawable.builder().buildRound(String.valueOf(posts.get(holder.getAdapterPosition()).author.charAt(0)).toUpperCase(), Color.BLUE));
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addToTop(Post post) {
        posts.add(0, post);
        notifyItemInserted(0);
    }

    public void removeItemAtPosition(int i) {
        posts.remove(i);
        notifyItemRemoved(i);
    }

    public void editItemAtPosition(int i, Post post) {
        posts.remove(i);
        posts.add(i, post);
        notifyItemChanged(i);
    }

    public String getUidAt(int position) {
        return posts.get(position).uid;
    }

    public int getCommentCount(int position) {
        return posts.get(position).comment_count;
    }

    public String getMessageAt(int position) {
        return posts.get(position).message;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentCount, realPost, postedBy, time;
        CircleImageView profile;

        public ViewHolder(View itemView) {
            super(itemView);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
            realPost = (TextView) itemView.findViewById(R.id.realPost);
            postedBy = (TextView) itemView.findViewById(R.id.postedBy);
            time = (TextView) itemView.findViewById(R.id.forumTime);
            profile = (CircleImageView) itemView.findViewById(R.id.forumPic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.itemClicked(view, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.itemLongClicked(v, getAdapterPosition());
                    return true;
                }
            });
        }
    }
}