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
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;


public class GraphFeedAdapter extends RecyclerView.Adapter<GraphFeedAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> messages, poster,
            likes, comments, postID;

    ClickListener clickListener;
    int type_header = 0;
    AppCompatEditText postText;
    int type_normal = 1;


    public GraphFeedAdapter(Context context, ArrayList<String> poster, ArrayList<String> messages,
                            ArrayList<String> likes, ArrayList<String> comments, ArrayList<String> postID) {
        this.context = context;
        this.messages = messages;
        this.poster = poster;
        this.likes = likes;
        this.comments = comments;
        this.postID = postID;
        inflater = LayoutInflater.from(context);

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == type_header)
            return new ViewHolder(inflater.inflate(R.layout.post_feed_header, parent, false));
        else
            return new ViewHolder(inflater.inflate(R.layout.each_feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            ProfilePictureView profPic = (ProfilePictureView) holder.core.findViewById(R.id.user_profpic);
            postText = (AppCompatEditText) holder.core.findViewById(R.id.userPostText);
            final RobotoTextView postButton = (RobotoTextView) holder.core.findViewById(R.id.user_post_button);
            try {
                profPic.setProfileId(Profile.getCurrentProfile().getId());
            } catch (Exception ignored) {
            }
            postButton.setEnabled(false);
            postText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() != 0)
                        postButton.setEnabled(true);
                    else postButton.setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendPostRequestThroughGraph(postText.getText().toString());
                }
            });
            return;
        }
        position--;
        holder.likeCount.setText(likes.get(position)+" likes");
        holder.commentCount.setText(comments.get(position)+" comments");
        holder.postedBy.setText(poster.get(position));
        holder.realPost.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return type_header;
        else
            return type_normal;
    }

    public interface ClickListener {
        void itemClicked(View view, int position);
        void newAdded();
    }

    private void sendPostRequestThroughGraph(String message) {
        Bundle params = new Bundle();
        params.putString("message", message);
        final String requestId = "CSITentrance/feed";
        new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if (graphResponse.getError() != null) {
                    Snackbar.make(MainActivity.mainLayout, "Unable to post.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(MainActivity.mainLayout, "Post successful.", Snackbar.LENGTH_SHORT).show();
                    clickListener.newAdded();
                    postText.setText("");
                }
            }
        }).executeAsync();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentCount;
        TextView likeCount;
        TextView realPost;
        TextView postedBy;
        LinearLayout core;

        public ViewHolder(View itemView) {
            super(itemView);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            realPost = (TextView) itemView.findViewById(R.id.realPost);
            postedBy = (TextView) itemView.findViewById(R.id.postedBy);
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