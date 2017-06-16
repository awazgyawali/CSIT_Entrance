package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.LeaderboardAdapter;
import np.com.aawaz.csitentrance.interfaces.ResponseListener;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.services.NetworkRequester;


public class LeaderBoard extends Fragment {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    RecyclerView mRecyclerView;
    ProgressBar progress;

    CircleImageView image1, image2, image3;
    TextView score1, score2, score3;
    TextView name1, name2, name3;

    TextView[] topNames;

    TextView[] topScores;

    CircleImageView[] circleImageViews;

    private LinearLayout errorLayout, coreLeader;
    private boolean filled = false;

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.leaderboardRecyclerView);
        errorLayout = (LinearLayout) view.findViewById(R.id.errorScore);
        progress = (ProgressBar) view.findViewById(R.id.progressbarScore);
        coreLeader = (LinearLayout) view.findViewById(R.id.coreLeaderBoard);

        image1 = (CircleImageView) view.findViewById(R.id.image1);
        image2 = (CircleImageView) view.findViewById(R.id.image2);
        image3 = (CircleImageView) view.findViewById(R.id.image3);

        name1 = (TextView) view.findViewById(R.id.name1);
        name2 = (TextView) view.findViewById(R.id.name2);
        name3 = (TextView) view.findViewById(R.id.name3);

        score1 = (TextView) view.findViewById(R.id.score1);
        score2 = (TextView) view.findViewById(R.id.score2);
        score3 = (TextView) view.findViewById(R.id.score3);

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
        mRecyclerView.setAdapter(new LeaderboardAdapter(getActivity(), names, scores));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        fillFromLastResponse();
        fetchFromInternet();
    }


    private void fetchFromInternet() {
        if (!filled) {
            progress.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            coreLeader.setVisibility(View.GONE);
        }

        new NetworkRequester(getString(R.string.fetchScoreurl), new ResponseListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    parser(new JSONObject(response));
                    SPHandler.getInstance().setLeaderBoardLastResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure() {
                if (!filled) {
                    errorLayout.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            }
        });
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
                    scores.add(jo_inside.getInt("score"));
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
}

