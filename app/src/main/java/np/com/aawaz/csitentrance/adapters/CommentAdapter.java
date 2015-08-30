package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> implements View.OnClickListener {

    private static int typeHeader = 1;
    private static int typeNormal = 2;
    private static int typeFooter = 3;
    ArrayList<String> commenter, comment, commenterId;
    LayoutInflater inflater;
    View header, footer;

    public CommentAdapter(Context context, ArrayList<String> commenter,
                          ArrayList<String> comment, ArrayList<String> commenterId, View header, View footer) {
        this.comment = comment;
        this.commenter = commenter;
        this.commenterId = commenterId;
        this.footer = footer;
        this.header = header;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == typeHeader)
            return new ViewHolder(header);
        else if (viewType == typeNormal)
            return new ViewHolder(inflater.inflate(R.layout.comment_each, parent, false));
        else
            return new ViewHolder(footer);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0 || position == getItemCount() - 1)
            return;
        holder.comment.setText(comment.get(position - 1));
        header.setOnClickListener(this);
        holder.commenter.setText(commenter.get(position - 1));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return typeHeader;
        else if (position <= commenter.size())
            return typeNormal;
        else
            return typeFooter;
    }

    @Override
    public int getItemCount() {
        return commenter.size() + 2;
    }

    @Override
    public void onClick(View view) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView commenter, comment;

        public ViewHolder(View itemView) {
            super(itemView);
            commenter = (TextView) itemView.findViewById(R.id.commenter);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }
}
