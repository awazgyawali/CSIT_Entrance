package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.devspark.robototextview.widget.RobotoTextView;
import com.truizlop.fabreveallayout.FABRevealLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.ForumAdapter;
import np.com.aawaz.csitentrance.fragments.other_fragments.CommentsFragment;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.SPHandler;
import np.com.aawaz.csitentrance.misc.Singleton;


public class EntranceForum extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    AppCompatEditText questionEditText;
    RelativeLayout mainLayout;
    ProgressBar progressBar;
    LinearLayout errorPart;
    FloatingActionButton fab;

    ImageView cross;
    ProgressBar posting;
    RobotoTextView postingResult;
    FABRevealLayout revealLayout;
    ForumAdapter adapter;

    private InputMethodManager imm;

    ArrayList<String> poster = new ArrayList<>(),
            messages = new ArrayList<>(),
            time = new ArrayList<>(),
            image_link = new ArrayList<>();

    ArrayList<Integer> postId = new ArrayList<>(),
            comments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        revealLayout = (FABRevealLayout) view.findViewById(R.id.fab_reveal_layout_forum);
        cross = (ImageView) view.findViewById(R.id.crossForum);
        posting = (ProgressBar) view.findViewById(R.id.progressBarForum);
        postingResult = (RobotoTextView) view.findViewById(R.id.resultHolderForum);
        errorPart = (LinearLayout) view.findViewById(R.id.errorPart);
        progressBar = (ProgressBar) view.findViewById(R.id.progressCircleFullFeed);
        recyclerView = (RecyclerView) view.findViewById(R.id.fullFeedRecycler);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshForum);
        fab = (FloatingActionButton) view.findViewById(R.id.fabAddForum);
        mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayoutForum);
        questionEditText = (AppCompatEditText) view.findViewById(R.id.questionEditText);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRegular();
            }
        });
        fab.hide();

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revealLayout.revealMainView();
            }
        });

        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    fab.hide();
                else
                    fab.show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        errorPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchFromServer();
                errorPart.setVisibility(View.GONE);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(questionEditText.getWindowToken(), 0);
                revealLayout.revealSecondaryView();
                sendPostRequestThroughGraph(questionEditText.getText().toString());
            }
        });
        fab.hide();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        fillDataFromDB();
    }

    private void fillDataFromDB() {
        errorPart.setVisibility(View.GONE);
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        Cursor cursor = database.query("forum", new String[]{"id", "poster", "message", "time", "image_link", "comments"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            postId.add(cursor.getInt(cursor.getColumnIndex("id")));
            comments.add(cursor.getInt(cursor.getColumnIndex("comments")));

            poster.add(cursor.getString(cursor.getColumnIndex("poster")));
            messages.add(cursor.getString(cursor.getColumnIndex("message")));
            time.add(cursor.getString(cursor.getColumnIndex("time")));
            image_link.add(cursor.getString(cursor.getColumnIndex("image_link")));
        }
        if (cursor.getCount() == 0)
            fetchFromServer();
        else {
            if (Singleton.isNwConnected(getContext()))
                fetchRegular();
            fillRecyclerView();
        }
        cursor.close();
    }

    private void fetchRegular() {
        setRefreshing(true);
        JsonArrayRequest request = new JsonArrayRequest(getString(R.string.getForum), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > poster.size()) {
                    for (int i = (response.length() - poster.size() - 1); i >= 0; i--) {
                        try {
                            JSONObject object = response.getJSONObject(i);

                            postId.add(0, object.getInt("id"));
                            poster.add(0, object.getString("poster"));
                            messages.add(0, object.getString("message"));
                            time.add(0, object.getString("time"));
                            image_link.add(0, object.getString("image_link"));
                            comments.add(0, object.getInt("comments"));

                            adapter.addToTop();
                            recyclerView.smoothScrollToPosition(0);

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
                setRefreshing(false);
            }
        });
        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void fillRecyclerView() {
        storeToDb();
        progressBar.setVisibility(View.GONE);
        adapter = new ForumAdapter(getContext(), poster, messages, comments, time, image_link);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CommentsFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("post_id", postId.get(position));
                bundle.putInt("comments", comments.get(position));

                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    private void storeToDb() {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SQLiteDatabase database = Singleton.getInstance().getDatabase();
                database.delete("forum", null, null);
                ContentValues values = new ContentValues();
                for (int i = 0; i < messages.size(); i++) {
                    values.clear();
                    values.put("id", postId.get(i));
                    values.put("comments", comments.get(i));
                    values.put("poster", poster.get(i));
                    values.put("message", messages.get(i));
                    values.put("time", time.get(i));
                    values.put("image_link", image_link.get(i));
                    database.insert("forum", null, values);
                }
                return null;
            }
        });
    }

    private void fetchFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        JsonArrayRequest request = new JsonArrayRequest(getString(R.string.getForum), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                mainLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);

                        poster.add(object.getString("poster"));
                        messages.add(object.getString("message"));
                        time.add(object.getString("time"));
                        image_link.add(object.getString("image_link"));
                        postId.add(object.getInt("id"));
                        comments.add(object.getInt("comments"));
                    } catch (Exception ignored) {
                    }
                }
                fillRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorPart.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void sendPostRequestThroughGraph(final String message) {
        posting.setVisibility(View.VISIBLE);
        postingResult.setVisibility(View.GONE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.postForum), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                postingResult.setText("Unable to post.\nCheck your internet connection.");
                postingResult.setVisibility(View.VISIBLE);
                posting.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", SPHandler.getInstance().getEmail());
                params.put("message", message);
                return params;
            }
        };

        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void setRefreshing(final boolean resfeshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(resfeshing);
            }
        });
    }
}