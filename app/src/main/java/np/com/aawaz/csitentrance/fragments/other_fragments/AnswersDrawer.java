package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.AnswerAdapter;

public class AnswersDrawer extends Fragment {
    RecyclerView recyclerView;
    AnswerAdapter adapter;

    public AnswersDrawer() {
        // Required empty public constructor
    }

    public static AnswersDrawer newInstance(int param1) {
        AnswersDrawer fragment = new AnswersDrawer();
        Bundle args = new Bundle();
        args.putInt("position", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.ansRecycle);

    }

    public void setInitialData(int qNo, int code) {
        adapter = new AnswerAdapter(getContext(), qNo, code);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    public void setInitialForSubject(int qNo, int code, int startFrom) {
        adapter = new AnswerAdapter(getContext(), qNo, code, startFrom);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    public void increaseSize() {
        adapter.increaseSize();
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_answers_drawer, container, false);
    }

}
