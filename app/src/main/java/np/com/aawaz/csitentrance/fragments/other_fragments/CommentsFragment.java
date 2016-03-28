package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.devspark.robototextview.widget.RobotoTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CommentAdapter;
import np.com.aawaz.csitentrance.misc.Singleton;

public class CommentsFragment extends BottomSheetDialogFragment {

    RecyclerView commentsRecyclerView;
    ProgressBar progressBar;

    ArrayList<String> comments = new ArrayList<>(),
            commenter = new ArrayList<>(),
            image_link = new ArrayList<>(),
            time = new ArrayList<>();

    AppCompatEditText commentEditText;
    ImageButton commentButton;
    CircleImageView profileImage;
    RobotoTextView commentNumber;

    public CommentsFragment() {
        // Required empty public constructor
    }

    private void readyCommentButton() {
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    commentButton.setEnabled(false);
                else
                    commentButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View views) {
                //todo post comment
            }
        });
    }

    public void onViewCreated(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressbarSingleFeed);
        commentsRecyclerView = (RecyclerView) view.findViewById(R.id.commentsRecyOfFullPost);
        commentEditText = (AppCompatEditText) view.findViewById(R.id.addCommentText);
        commentButton = (ImageButton) view.findViewById(R.id.commentButton);
        profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
        commentNumber= (RobotoTextView) view.findViewById(R.id.numberComments);
    }

    private void fillViews() throws Exception {
        progressBar.setVisibility(View.GONE);
        commentsRecyclerView.setVisibility(View.VISIBLE);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CommentAdapter adapter = new CommentAdapter(getActivity(), commenter, comments, image_link, time);
        commentsRecyclerView.setAdapter(adapter);
        readyCommentButton();
    }

    private void fetchFromInternet() {
        int requestId = getArguments().getInt("post_id");
        commentNumber.setText(getArguments().getInt("comments")+ " Comments");
        JsonArrayRequest request = new JsonArrayRequest(getActivity().getString(R.string.getComment) + requestId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject commentObject = response.getJSONObject(i);
                        comments.add(commentObject.getString("comment"));
                        commenter.add(commentObject.getString("name"));
                        time.add(commentObject.getString("time"));
                        image_link.add(commentObject.getString("image_link"));
                    }
                    fillViews();
                } catch (Exception ignored) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Singleton.getInstance().getRequestQueue().add(request);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_comments, null);
        dialog.setContentView(contentView);
        onViewCreated(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        commentsRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        profileImage.setImageURI(Uri.parse(getContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("ImageLink", "")));
        fetchFromInternet();
    }
}
