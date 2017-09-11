package np.com.aawaz.csitentrance.fragments.navigation_fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
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

    private String mUrl;
    private String mTitle;
    private SwipeRefreshLayout swipeRefresh;

    public EntranceFAQs() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyFaq);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.faqsSwipeRefresh);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_faq);
        error = (LinearLayout) view.findViewById(R.id.error_faq);
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyOneTimeListener();
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                readyOneTimeListener();
            }
        });
        appIndexing();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readyOneTimeListener();
        FirebaseDatabase.getInstance().getReference().child("faqs").keepSynced(true);
    }

    private void readyOneTimeListener() {
        adapter = new FAQAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("faqs").addListenerForSingleValueEvent(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrance_faqs, container, false);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            FAQ faq = child.getValue(FAQ.class);
            adapter.add(faq);
            mTitle = faq.question;
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        progressBar.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/forum";
        mTitle = "CSIT Entrance FAQs";
    }

    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

}
