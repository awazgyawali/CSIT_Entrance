package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.FeaturedCollegeAdapter;
import np.com.aawaz.csitentrance.objects.FeaturedCollege;

public class FeaturedColleges extends Fragment implements ValueEventListener {

    RecyclerView recyclerView;
    DatabaseReference reference;
    FeaturedCollegeAdapter adapter;
    private ProgressBar progressBar;

    public FeaturedColleges() {
        // Required empty public constructor
    }

    public static FeaturedColleges newInstance() {
        return new FeaturedColleges();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readyAdapter();
        attachOnTimeListener();
    }

    private void attachOnTimeListener() {
        reference = FirebaseDatabase.getInstance().getReference().child("featured_college_ad");
        reference.addListenerForSingleValueEvent(this);
    }

    private void readyAdapter() {
        adapter = new FeaturedCollegeAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                if (position == 0)
                    outRect.top = getResources().getDimensionPixelSize(R.dimen.first_item);
                else
                    outRect.top = getResources().getDimensionPixelSize(R.dimen.normal_item);

                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.normal_item);

                outRect.left = getResources().getDimensionPixelSize(R.dimen.first_item);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.first_item);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_featured_college, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_featured);
        progressBar = (ProgressBar) view.findViewById(R.id.loading_featured);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        for (DataSnapshot child : dataSnapshot.getChildren())
            adapter.addToTop(child.getValue(FeaturedCollege.class));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(getContext(), "Unable to load data.", Toast.LENGTH_SHORT).show();
    }
}
