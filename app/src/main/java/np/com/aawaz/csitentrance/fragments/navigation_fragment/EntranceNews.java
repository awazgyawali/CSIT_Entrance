package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.NewsAdapter;
import np.com.aawaz.csitentrance.fragments.other_fragments.EachNews;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.Singleton;


public class EntranceNews extends Fragment {

    ArrayList<String> time = new ArrayList<>(),
            title = new ArrayList<>(),
            imageUrl = new ArrayList<>(),
            messages = new ArrayList<>();

    RecyclerView recy;
    RequestQueue requestQueue;
    NewsAdapter newsAdapter;
    private LinearLayout errorLayout;
    ProgressBar progress;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setting the data
        setDataToArrayListFromDb();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy = (RecyclerView) view.findViewById(R.id.newsFeedRecy);
        errorLayout = (LinearLayout) view.findViewById(R.id.errorNews);
        progress = (ProgressBar) view.findViewById(R.id.progressbarNews);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshNews);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRegular();
            }
        });
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDataToArrayListFromDb();
            }
        });
    }

    public void fillData() {
        errorLayout.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        recy.setVisibility(View.VISIBLE);
        newsAdapter = new NewsAdapter(getContext(), title, messages, time, imageUrl);
        recy.setLayoutManager(new LinearLayoutManager(getContext()));
        recy.setItemAnimator(new SlideInLeftAnimator());
        recy.setAdapter(newsAdapter);
        newsAdapter.setOnClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {

                BottomSheetDialogFragment bottomSheetDialogFragment = new EachNews();

                Bundle bundle = new Bundle();
                bundle.putString("title", title.get(position));
                bundle.putString("detail", messages.get(position));
                bundle.putString("time", time.get(position));
                bundle.putString("image_link", imageUrl.get(position));

                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    public void setDataToArrayListFromDb() {
        progress.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        recy.setVisibility(View.GONE);

        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        Cursor cursor = database.query("news", new String[]{"title", "message", "time", "imageURL"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            time.add(cursor.getString(cursor.getColumnIndex("time")));
            messages.add(cursor.getString(cursor.getColumnIndex("message")));
            imageUrl.add(cursor.getString(cursor.getColumnIndex("imageURL")));
            title.add(cursor.getString(cursor.getColumnIndex("title")));
        }
        if (cursor.getCount() == 0)
            fetchNewsForFirstTime();
        else {
            if (Singleton.isNwConnected(getContext()))
                fetchRegular();
            fillData();
        }

        cursor.close();
    }

    private void fetchRegular() {
        setRefreshing(true);
        JsonArrayRequest request = new JsonArrayRequest(getString(R.string.newsLink), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > title.size()) {
                    for (int i = (response.length() - title.size() - 1); i >= 0; i--) {
                        try {
                            JSONObject object = response.getJSONObject(i);

                            messages.add(0, object.getString("notice"));
                            try {
                                imageUrl.add(0, object.getString("full_picture"));
                            } catch (Exception e) {
                                imageUrl.add(0, "null");
                            }
                            time.add(0, object.getString("time"));
                            title.add(0, object.getString("title"));

                            newsAdapter.addToTop();
                            recy.smoothScrollToPosition(0);

                            setRefreshing(false);
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                            setRefreshing(false);
                        }
                    }
                    storeToDb();
                } else {
                    setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                setRefreshing(false);
            }
        });
        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void setRefreshing(final boolean refreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }


    public void fetchNewsForFirstTime() {
        requestQueue = Singleton.getInstance().getRequestQueue();
        String link = getString(R.string.newsLink);

        JsonArrayRequest jsonRequest = new JsonArrayRequest(link,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseTheResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorLayout.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        recy.setVisibility(View.GONE);
                    }
                });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    private void storeToDb() {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SQLiteDatabase database = Singleton.getInstance().getDatabase();
                database.delete("news", null, null);
                ContentValues values = new ContentValues();
                for (int i = 0; i < messages.size(); i++) {
                    values.clear();
                    values.put("time", time.get(i));
                    values.put("message", messages.get(i));
                    values.put("imageURL", imageUrl.get(i));
                    values.put("title", title.get(i));
                    database.insert("news", null, values);
                }
                return null;
            }
        });
    }

    private void parseTheResponse(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                try {
                    messages.add(object.getString("notice"));
                    try {
                        imageUrl.add(object.getString("full_picture"));
                    } catch (Exception e) {
                        imageUrl.add("null");
                    }
                    time.add(object.getString("time"));
                    title.add(object.getString("title"));
                } catch (Exception ignored) {
                }
            }
            fillData();
            storeToDb();
        } catch (Exception ignored) {
            errorLayout.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            recy.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }
}