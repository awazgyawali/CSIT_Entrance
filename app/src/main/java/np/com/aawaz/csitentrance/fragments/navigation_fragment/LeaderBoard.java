package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
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
import np.com.aawaz.csitentrance.adapters.LeaderboardAdapter;
import np.com.aawaz.csitentrance.objects.SPHandler;


public class LeaderBoard extends Fragment {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<Long> scores = new ArrayList<>();
    ArrayList<String> image_url = new ArrayList<>();
    RecyclerView mRecyclerView;
    ProgressBar progress;

    CircleImageView image1, image2, image3;
    TextView score1, score2, score3;
    TextView name1, name2, name3;

    TextView[] topNames;

    TextView[] topScores;

    CircleImageView[] circleImageViews;

    private LinearLayout errorLayout;
    NestedScrollView coreLeader;
    private boolean filled = false;
    JSONObject object = new JSONObject();

    private void fillFromLastResponse() {
        String response = SPHandler.getInstance().getLeaderBoardLastResponse();
        if (response != null) {
            try {
                parser(new JSONObject(response));
                filled = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
        mRecyclerView.setAdapter(new LeaderboardAdapter(getActivity(), names, scores, image_url));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchFromInternet();
    }

    private void fetchFromInternet() {
        if (!filled) {
            progress.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            coreLeader.setVisibility(View.GONE);
        }

        FirebaseFirestore.getInstance().collection("users").orderBy("score", Query.Direction.DESCENDING).limit(10).get()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            if (task.isSuccessful()) {
                                int i = 0;
                                JSONArray array = new JSONArray();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> user = document.getData();
                                    JSONObject userDetail = new JSONObject();
                                    userDetail.put("name", user.get("name"));
                                    userDetail.put("score", String.valueOf(user.get("score")));
                                    userDetail.put("image_link", user.get("image_url"));
                                    if (i < 3) {
                                        topNames[i].setText((String) user.get("name"));
                                        topScores[i].setText(String.valueOf(user.get("score")));
                                        Picasso.with(getContext())
                                                .load((String) user.get("image_url"))
                                                .placeholder(R.drawable.account_holder)
                                                .into(circleImageViews[i]);
                                    } else {
                                        names.add((String) user.get("name"));
                                        scores.add((Long) user.get("score"));
                                        image_url.add((String) user.get("image_url"));
                                    }
                                    i++;
                                    array.put(userDetail);
                                }
                                object.put("scores", array);
                                callFillRecyclerView();
                                saveResponse(object);
                            } else {
                                getFromBackup();
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

