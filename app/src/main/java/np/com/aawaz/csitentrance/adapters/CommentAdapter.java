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
import np.com.aawaz.csitentrance.misc.MyApplication;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<String> commenter, image_link, time, comment;
    LayoutInflater inflater;

    public CommentAdapter(Context context, ArrayList<String> commenter,
                          ArrayList<String> comment, ArrayList<String> image_link, ArrayList<String> time) {
        this.comment = comment;
        this.commenter = commenter;
        this.image_link = image_link;
        this.time = time;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.comment_each, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.comment.setText(comment.get(position));
        holder.commenter.setText(commenter.get(position));
        holder.time.setText(time.get(position));
        Picasso.with(MyApplication.getAppContext())
                .load(image_link.get(position))
                .into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return commenter.size();
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
