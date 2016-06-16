package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.NewsAdapter;
import np.com.aawaz.csitentrance.objects.News;


public class EntranceNews extends Fragment implements ValueEventListener {

    RecyclerView recy;
    NewsAdapter newsAdapter;
    private LinearLayout errorLayout;
    ProgressBar progress;
    DatabaseReference reference;


    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setting the data
        readyAdapter();
        addOneTimeListener();
        appIndexing();
    }


    private void appIndexing() {
        mClient = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        mUrl = Uri.parse("http://csitentrance.brainants.com/news");
        mTitle = "Latest news about the BSc CSIT entrance exam.";
        mDescription = "All the information you need to know about the BSc CSIT admission process";
    }


    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(mUrl)
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getAction());
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end(mClient, getAction());
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy = (RecyclerView) view.findViewById(R.id.newsFeedRecy);
        errorLayout = (LinearLayout) view.findViewById(R.id.errorNews);
        progress = (ProgressBar) view.findViewById(R.id.progressbarNews);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneTimeListener();
            }
        });
    }

    public void readyAdapter() {
        newsAdapter = new NewsAdapter(getContext());
        recy.setLayoutManager(new LinearLayoutManager(getContext()));
        recy.setAdapter(newsAdapter);
        reference = FirebaseDatabase.getInstance().getReference().child("news");
    }

    private void addOneTimeListener() {
        errorLayout.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        reference.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren())
            newsAdapter.addToTop(child.getValue(News.class));
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        progress.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }
}