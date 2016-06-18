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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.FAQAdapter;
import np.com.aawaz.csitentrance.objects.FAQ;

public class EntranceFAQs extends Fragment implements ValueEventListener {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout error;
    FAQAdapter adapter;

    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;

    public EntranceFAQs() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyFaq);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_faq);
        error = (LinearLayout) view.findViewById(R.id.error_faq);
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyOneTimeListener();
            }
        });
        appIndexing();
    }

    private void appIndexing() {
        mClient = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        mUrl = Uri.parse("http://csitentrance.brainants.com/forum");
        mTitle = "CSIT Entrance FAQs";
        mDescription = "FAQs about the entrance.";
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
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end(mClient, getAction());
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readyAdapter();

        readyOneTimeListener();
    }

    private void readyOneTimeListener() {
        progressBar.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("faqs").addListenerForSingleValueEvent(this);
    }

    private void readyAdapter() {
        adapter = new FAQAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrance_faqs, container, false);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            FAQ faq = child.getValue(FAQ.class);
            adapter.add(faq);
            mTitle = faq.question;
            AppIndex.AppIndexApi.start(mClient, getAction());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        progressBar.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
    }
}
