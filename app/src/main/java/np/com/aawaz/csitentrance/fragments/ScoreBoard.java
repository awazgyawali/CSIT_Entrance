package np.com.aawaz.csitentrance.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.ScoreBoardAdapter;
import np.com.aawaz.csitentrance.misc.BackgroundTaskHandler;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.misc.Singleton;


public class ScoreBoard extends Fragment {

    ArrayList<String> names=new ArrayList<>(),
            images = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    RequestQueue requestQueue;
    RecyclerView mRecyclerView;
    ProgressBar progress;

    private LinearLayout errorLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchFromInternet();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.scoreBoardRecyclerView);
        errorLayout = (LinearLayout) view.findViewById(R.id.errorScore);
        progress = (ProgressBar) view.findViewById(R.id.progressbarScore);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchFromInternet();
            }
        });
    }

    private void callFillRecyclerView() {
        progress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        mRecyclerView.setAdapter(new ScoreBoardAdapter(getActivity(), names, scores, images));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
        errorLayout.setVisibility(View.GONE);
    }

    private void fetchFromInternet() {
        progress.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        requestQueue = Singleton.getInstance().getRequestQueue();
        String url = getString(R.string.fetchScoreurl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("scores");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        names.add(jo_inside.getString("Name"));
                        scores.add(jo_inside.getInt("Score"));
                        images.add(jo_inside.getString("ImageLink"));
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

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scoreboard, container, false);
    }
}

