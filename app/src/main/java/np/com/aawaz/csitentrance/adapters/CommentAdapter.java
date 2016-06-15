package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.objects.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<Comment> comments = new ArrayList<>();

    LayoutInflater inflater;

    public CommentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.comment_each, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.comment.setText(comments.get(position).message);
        holder.commenter.setText(comments.get(position).author);
        holder.time.setText(DateUtils.getRelativeTimeSpanString(comments.get(position).time_stamp, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
        Picasso.with(MyApplication.getAppContext())
                .load(comments.get(position).image_url)
                .into(holder.circleImageView);
    }

    public void addToTop(Comment comment) {
        comments.add(comment);
        notifyItemInserted(getItemCount() - 1);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView commenter, comment, time;
        CircleImageView circleImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            commenter = (RobotoTextView) itemView.findViewById(R.id.commenter);
            comment = (RobotoTextView) itemView.findViewById(R.id.comment);
            time = (RobotoTextView) itemView.findViewById(R.id.timeComment);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profileComment);
        }
    }
}
