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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.LeaderboardAdapter;
import np.com.aawaz.csitentrance.misc.Singleton;


public class LeaderBoard extends Fragment {

    ArrayList<String> names = new ArrayList<>(),
            images = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    RequestQueue requestQueue;
    RecyclerView mRecyclerView;
    ProgressBar progress;

    CircleImageView image1, image2, image3;
    RobotoTextView score1, score2, score3;
    RobotoTextView name1, name2, name3;

    RobotoTextView[] topNames;

    RobotoTextView[] topScores;

    CircleImageView[] circleImageViews;

    private LinearLayout errorLayout, coreLeader;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchFromInternet();
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

        name1 = (RobotoTextView) view.findViewById(R.id.name1);
        name2 = (RobotoTextView) view.findViewById(R.id.name2);
        name3 = (RobotoTextView) view.findViewById(R.id.name3);

        score1 = (RobotoTextView) view.findViewById(R.id.score1);
        score2 = (RobotoTextView) view.findViewById(R.id.score2);
        score3 = (RobotoTextView) view.findViewById(R.id.score3);

        topNames = new RobotoTextView[]{name1, name2, name3};
        topScores = new RobotoTextView[]{score1, score2, score3};
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
        mRecyclerView.setAdapter(new LeaderboardAdapter(getActivity(), names, scores, images));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        errorLayout.setVisibility(View.GONE);
    }

    private void fetchFromInternet() {
        progress.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        coreLeader.setVisibility(View.GONE);

        requestQueue = Singleton.getInstance().getRequestQueue();
        String url = getString(R.string.fetchScoreurl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("scores");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);

                        if (i < 3) {
                            topNames[i].setText(jo_inside.getString("name"));
                            topScores[i].setText(String.valueOf(jo_inside.getInt("score")));
                            Picasso.with(getContext())
                                    .load(jo_inside.getString("image_link"))
                                    .into(circleImageViews[i]);
                        } else {
                            names.add(jo_inside.getString("name"));
                            scores.add(jo_inside.getInt("score"));
                            images.add(jo_inside.getString("image_link"));
                        }
                    }
                    callFillRecyclerView();
                } catch (JSONException e) {
                    errorLayout.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });
        requestQueue.add(jsonObjectRequest.setTag("score"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }
}

