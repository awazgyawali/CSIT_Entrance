package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.ImageViewingActivity;
import np.com.aawaz.csitentrance.activities.ProfileActivity;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.GlideApp;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.objects.Comment;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private boolean shouldShowUpvote = true;
    ArrayList<Comment> comments = new ArrayList<>();

    LayoutInflater inflater;
    private Context context;
    private ClickListener clickEventListener;

    public CommentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public CommentAdapter(Context context, boolean shouldShowUpvote) {
        this(context);
        this.shouldShowUpvote = shouldShowUpvote;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.each_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Comment comment = comments.get(holder.getAdapterPosition());
        if(comment.author==null)
            comment.author = "Deleted Comment";
        holder.comment.setText(comment.message);
        holder.commenter.setText(comment.author + " ");
        holder.time.setText(DateUtils.getRelativeTimeSpanString(comment.time_stamp, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));

        if (SPHandler.containsDevUID(comment.uid))
            holder.commenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.admin, 0);
        else
            holder.commenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (comment.attached_image != null) {
            holder.attachedImage.setVisibility(View.VISIBLE);
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("discussion/" + comment.attached_image);
            GlideApp.with(context).load(ref).placeholder(R.drawable.placeholder).into(holder.attachedImage);
            holder.attachedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ImageViewingActivity.class).putExtra("path", comment.attached_image).putExtra("local", false));
                }
            });
        } else {
            holder.attachedImage.setVisibility(View.GONE);
            holder.attachedImage.setOnClickListener(null);
        }

        if (comment.likes != null) {
            holder.upvote.setText(String.valueOf(comment.likes.size()));
            if (comment.likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.upvote.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.upvote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.upvote_accent, 0);
            } else {
                holder.upvote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.upvote_grey, 0);
                holder.upvote.setTextColor(ContextCompat.getColor(context, R.color.grey));
            }
        } else {
            holder.upvote.setText("0");
            holder.upvote.setTextColor(ContextCompat.getColor(context, R.color.grey));
            holder.upvote.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.upvote_grey, 0);
        }

        if (!shouldShowUpvote) {
            holder.upvote.setVisibility(View.GONE);
        }
        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEventListener.upVoteClicked(view, holder.getAdapterPosition());
            }
        });

        try {
            Picasso.with(MyApplication.getAppContext())
                    .load(comment.image_url)
                    .placeholder(TextDrawable.builder().buildRound(String.valueOf(comment.author.charAt(0)).toUpperCase(), Color.BLUE))
                    .into(holder.circleImageView);
        } catch (Exception e) {
            if ( comment.author.length() > 0)
                holder.circleImageView.setImageDrawable(TextDrawable.builder().buildRound(String.valueOf(comment.author.charAt(0)).toUpperCase(), Color.BLUE));
        }

        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", comment.uid));

            }
        });

        holder.core.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickEventListener.itemLongClicked(v, holder.getAdapterPosition());
                return true;
            }
        });
    }


    public void addToTop(Comment comment) {
        comments.add(comment);
        notifyItemInserted(getItemCount() - 1);
    }

    public void setClickEventListener(ClickListener clickEventListener) {
        this.clickEventListener = clickEventListener;
    }

    public void removeItemAtPosition(int i) {
        comments.remove(i);
        notifyItemRemoved(i);
    }


    public void editItemAtPosition(int i, Comment comment) {
        comments.remove(i);
        comments.add(i, comment);
        notifyItemChanged(i);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public String getUidAt(int position) {
        return comments.get(position).uid;
    }

    public String getMessageAt(int position) {
        return comments.get(position).message;
    }

    public void upVoteAtPosition(int position) {
        List<String> currentLikes = comments.get(position).likes;
        if (currentLikes == null)
            currentLikes = new ArrayList<>();

        if (currentLikes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            currentLikes.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
        else
            currentLikes.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        comments.get(position).likes = currentLikes;
        notifyItemChanged(position);
    }

    public boolean haveIVoted(int position) {
        List<String> currentLikes = comments.get(position).likes;
        if (currentLikes == null)
            currentLikes = new ArrayList<>();

        return (currentLikes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView commenter, comment, time, upvote;
        CircleImageView circleImageView;
        ImageView attachedImage;
        View core;

        public ViewHolder(View itemView) {
            super(itemView);
            core = itemView;
            commenter = itemView.findViewById(R.id.commenter);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.timeComment);
            circleImageView = itemView.findViewById(R.id.profileComment);
            attachedImage = itemView.findViewById(R.id.attachedImage);
            upvote = itemView.findViewById(R.id.upvoteButton);
        }
    }
}
