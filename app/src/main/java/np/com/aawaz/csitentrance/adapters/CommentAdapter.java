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
        Comment comment = comments.get(holder.getAdapterPosition());
        holder.comment.setText(comment.message);
        holder.commenter.setText(comment.author);
        holder.time.setText(DateUtils.getRelativeTimeSpanString(comment.time_stamp, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comments.get(position).uid)) {
            holder.commenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_grey, 0);
        } else
            holder.commenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        if (SPHandler.containsDevUID(comments.get(position).uid)) {
            holder.commenter.setTextColor(ContextCompat.getColor(context, R.color.admin_color));
            holder.admin_tag.setVisibility(View.VISIBLE);
        } else {
            holder.commenter.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.admin_tag.setVisibility(View.GONE);
        }

        try {
            Picasso.with(MyApplication.getAppContext())
                    .load(comment.image_url)
                    .placeholder(TextDrawable.builder().buildRound(String.valueOf(comment.author.charAt(0)).toUpperCase(), Color.BLUE))
                    .into(holder.circleImageView);
        } catch (Exception e) {
            holder.circleImageView.setImageDrawable(TextDrawable.builder().buildRound(String.valueOf(comment.author.charAt(0)).toUpperCase(), Color.BLUE));
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView commenter, comment, time;
        CircleImageView circleImageView;
        View core;
        FancyButton admin_tag;

        public ViewHolder(View itemView) {
            super(itemView);
            core = itemView;
            admin_tag = (FancyButton) itemView.findViewById(R.id.comment_admin_tag);
            commenter = (TextView) itemView.findViewById(R.id.commenter);
            comment = (TextView) itemView.findViewById(R.id.comment);
            time = (TextView) itemView.findViewById(R.id.timeComment);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profileComment);
        }
    }
}
