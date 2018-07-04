package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.ProfileActivity;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.objects.Comment;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<Comment> comments = new ArrayList<>();

    LayoutInflater inflater;
    private Context context;
    private ClickListener clickEventListener;

    public CommentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.each_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Comment comment = comments.get(holder.getAdapterPosition());
        holder.comment.setText(comment.getMessage());
        holder.commenter.setText(comment.getAuthor() + " ");
        holder.time.setText(DateUtils.getRelativeTimeSpanString(comment.getTime_stamp(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comments.get(position).getUid())) {
            holder.editComment.setVisibility(View.VISIBLE);
        } else
            holder.editComment.setVisibility(View.GONE);


        if (SPHandler.containsDevUID(comment.getUid()))
            holder.commenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.admin, 0);
        else
            holder.commenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


        try {
            Picasso.with(MyApplication.getAppContext())
                    .load(comment.getImage_url())
                    .placeholder(TextDrawable.builder().buildRound(String.valueOf(comment.getAuthor().charAt(0)).toUpperCase(), Color.BLUE))
                    .into(holder.circleImageView);
        } catch (Exception e) {
            holder.circleImageView.setImageDrawable(TextDrawable.builder().buildRound(String.valueOf(comment.getAuthor().charAt(0)).toUpperCase(), Color.BLUE));
        }

        holder.editComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEventListener.itemLongClicked(view, holder.getAdapterPosition());
            }
        });

        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", comment.getUid()));

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
        return comments.get(position).getUid();
    }

    public String getMessageAt(int position) {
        return comments.get(position).getMessage();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView commenter, comment, time;
        CircleImageView circleImageView;
        View core;
        ImageView editComment;

        public ViewHolder(View itemView) {
            super(itemView);
            core = itemView;
            commenter = itemView.findViewById(R.id.commenter);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.timeComment);
            circleImageView = itemView.findViewById(R.id.profileComment);
            editComment = itemView.findViewById(R.id.editIcon);
        }
    }
}
