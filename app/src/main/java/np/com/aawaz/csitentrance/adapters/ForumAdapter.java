package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;


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
        Post post = posts.get(holder.getAdapterPosition());
        holder.postedBy.setText(post.author);
        holder.realPost.setText(post.message);
        holder.commentCount.setText(post.comment_count + getCommentMessage(post));
        holder.time.setText(getRelativeTimeSpanString(post.time_stamp));

        if (SPHandler.containsDevUID(post.uid)) {
            holder.postedBy.setTextColor(ContextCompat.getColor(context, R.color.admin_color));
            holder.admin_tag.setVisibility(View.VISIBLE);
        } else {
            holder.postedBy.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.admin_tag.setVisibility(View.GONE);
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.uid))
            holder.postedBy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_grey, 0);
        else
            holder.postedBy.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        try {
            Picasso.with(MyApplication.getAppContext())
                    .load(post.image_url)
                    .error(TextDrawable.builder().buildRound(String.valueOf(post.author.charAt(0)).toUpperCase(), Color.BLUE))
                    .placeholder(TextDrawable.builder().buildRound(String.valueOf(post.author.charAt(0)).toUpperCase(), Color.BLUE))
                    .into(holder.profile);
        } catch (Exception e) {
            holder.profile.setImageDrawable(TextDrawable.builder().buildRound(String.valueOf(post.author.charAt(0)).toUpperCase(), Color.BLUE));
        }
    }

    private CharSequence getRelativeTimeSpanString(long time_stamp) {
        return DateUtils.getRelativeTimeSpanString(time_stamp, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
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

    public void editItemAtPosition(int i) {
        notifyItemChanged(i);
    }

    public Post getItemAtPosition(int i) {
        return posts.get(i);
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

    private String getCommentMessage(Post post) {
        return post.comment_count == 1 ? " comment" : " comments";
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentCount, realPost, postedBy, time;
        CircleImageView profile;
        FancyButton admin_tag;

        public ViewHolder(View itemView) {
            super(itemView);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
            realPost = (TextView) itemView.findViewById(R.id.realPost);
            postedBy = (TextView) itemView.findViewById(R.id.postedBy);
            admin_tag = (FancyButton) itemView.findViewById(R.id.post_admin_tag);
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