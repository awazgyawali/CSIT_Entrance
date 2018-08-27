package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.ProfileActivity;
import np.com.aawaz.csitentrance.adapters.LeaderboardAdapter;
import np.com.aawaz.csitentrance.objects.SPHandler;


public class ScoreLeaderboard extends Fragment {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<Long> scores = new ArrayList<>();
    ArrayList<String> image_url = new ArrayList<>();
    ArrayList<String> uids = new ArrayList<>();
    RecyclerView mRecyclerView;
    ProgressBar progress;

    CircleImageView image1, image2, image3;
    TextView score1, score2, score3;
    TextView name1, name2, name3;

    TextView myName, myScore;
    CircleImageView myImage;
    LinearLayout myCard;

    TextView[] topNames;

    TextView[] topScores;

    CircleImageView[] circleImageViews;
    NestedScrollView coreLeader;
    JSONObject object = new JSONObject();
    private LinearLayout errorLayout;
    private boolean filled = false;
    private Query query;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.leaderboardRecyclerView);
        errorLayout = view.findViewById(R.id.errorScore);
        progress = view.findViewById(R.id.progressbarScore);
        coreLeader = view.findViewById(R.id.coreLeaderBoard);

        image1 = view.findViewById(R.id.image1);
        image2 = view.findViewById(R.id.image2);
        image3 = view.findViewById(R.id.image3);

        name1 = view.findViewById(R.id.name1);
        name2 = view.findViewById(R.id.name2);
        name3 = view.findViewById(R.id.name3);

        score1 = view.findViewById(R.id.score1);
        score2 = view.findViewById(R.id.score2);
        score3 = view.findViewById(R.id.score3);

        topNames = new TextView[]{name1, name2, name3};
        topScores = new TextView[]{score1, score2, score3};
        circleImageViews = new CircleImageView[]{image1, image2, image3};

        myName = view.findViewById(R.id.scoreboardName);
        myImage = view.findViewById(R.id.leaderboard_item_image);
        myScore = view.findViewById(R.id.scoreboardScore);

        myCard = view.findViewById(R.id.myLeadeboardItem);

        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchFromInternet();
            }
        });
    }

    private void callFillRecyclerView() {
        progress.setVisibility(View.GONE);
        coreLeader.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        mRecyclerView.setAdapter(new LeaderboardAdapter(getActivity(), uids, names, scores, image_url));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchFromInternet();

        populateOwn();
    }

    private void populateOwn() {
        myName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Picasso.with(getContext())
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(myImage);

        myScore.setText(String.valueOf(SPHandler.getInstance().getTotalScore()));
    }

    private void fetchFromInternet() {
        if (!filled) {
            progress.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            coreLeader.setVisibility(View.GONE);
        }

        query =FirebaseFirestore.getInstance().collection("users").orderBy("score", Query.Direction.DESCENDING).limit(10);
               query .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        try {
                            names.clear();
                            uids.clear();
                            scores.clear();
                            image_url.clear();
                            int i = 0;
                            JSONArray array = new JSONArray();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                final Map<String, Object> user = document.getData();
                                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.get("uid"))) {
                                    myCard.setVisibility(View.GONE);
                                }
                                JSONObject userDetail = new JSONObject();
                                userDetail.put("name", user.get("name"));
                                userDetail.put("uid", user.get("uid"));
                                userDetail.put("score", String.valueOf(user.get("score")));
                                userDetail.put("image_link", user.get("image_url"));
                                if (i < 3) {
                                    topNames[i].setText((String) user.get("name"));
                                    topScores[i].setText(String.valueOf(user.get("score")));
                                    Picasso.with(getContext())
                                            .load((String) user.get("image_url"))
                                            .placeholder(R.drawable.account_holder)
                                            .into(circleImageViews[i]);
                                    circleImageViews[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(getContext(), ProfileActivity.class).putExtra("uid", user.get("uid").toString()));
                                        }
                                    });
                                } else {
                                    names.add((String) user.get("name"));
                                    uids.add((String) user.get("uid"));
                                    scores.add((Long) user.get("score"));
                                    image_url.add((String) user.get("image_url"));
                                }
                                i++;
                                array.put(userDetail);
                            }
                            if (array.length() > 2) {
                                object.put("scores", array);
                                callFillRecyclerView();
                                saveResponse(object);
                            } else {
                                errorLayout.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);
                            }
                        } catch (Exception ignored) {

                        }
                    }
                });
    }

    private void saveResponse(JSONObject object) {
        SPHandler.getInstance().setLeaderBoardLastResponse(object.toString());
    }

    private void parser(JSONObject response) {
        names.clear();
        scores.clear();
        try {
            JSONArray jsonArray = response.getJSONArray("scores");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                if (i < 3) {
                    topNames[i].setText(jo_inside.getString("name"));
                    topScores[i].setText(String.valueOf(jo_inside.getInt("score")));
                    Picasso.with(getContext())
                            .load(jo_inside.getString("image_link"))
                            .placeholder(R.drawable.account_holder)
                            .into(circleImageViews[i]);
                } else {
                    names.add(jo_inside.getString("name"));
                    scores.add(jo_inside.getLong("score"));
                }
            }
            callFillRecyclerView();
        } catch (Exception e) {
            if (!filled) {
                errorLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    public void getFromBackup() throws JSONException {
        String response = SPHandler.getInstance().getLeaderBoardLastResponse();
        if (response != null) {
            parser(new JSONObject(response));
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }
}

