package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.devspark.robototextview.widget.RobotoTextView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.Singleton;


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
        holder.commentCount.setText(comments.get(position) + " comments");
        holder.postedBy.setText(poster.get(position));
        holder.realPost.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size() + 1;
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