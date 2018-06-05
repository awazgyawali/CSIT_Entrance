package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.YearListAdapter;
import np.com.aawaz.csitentrance.objects.YearItem;

public class YearsList extends Fragment {

    private RecyclerView recyclerview;

    public YearsList() {
        // Required empty public constructor
    }

    public static YearsList newInstance() {
        return new YearsList();
    }

    ArrayList<YearItem> items = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview = view.findViewById(R.id.yearListRecy);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prepareCollegeModelQuestion();

        prepareOldQuestions();

        prepareModelQuestion();


        recyclerview.setAdapter(new YearListAdapter(getContext(), items));
    }

    private void prepareCollegeModelQuestion() {

        YearItem modelHeader = new YearItem();
        modelHeader.type = YearItem.SECTION_TITLE;
        modelHeader.title = "College Model Question";
        items.add(modelHeader);

        YearItem item7 = new YearItem();
        item7.type = YearItem.COLLEGE_MODEL;
        item7.title = "ACHS Model Question";
        item7.paperCode = 12;
        items.add(item7);

        YearItem item8 = new YearItem();
        item8.type = YearItem.COLLEGE_MODEL;
        item8.title = "Sagarmatha Model Question";
        item8.paperCode = 13;
        items.add(item8);

    }

    private void prepareModelQuestion() {
        YearItem modelHeader = new YearItem();
        modelHeader.type = YearItem.SECTION_TITLE;
        modelHeader.title = "Model Questions";
        items.add(modelHeader);

        YearItem item7 = new YearItem();
        item7.type = YearItem.YEAR_SET;
        item7.title = "Model 1";
        item7.paperCode = 6;
        items.add(item7);

        YearItem item8 = new YearItem();
        item8.type = YearItem.YEAR_SET;
        item8.title = "Model 2";
        item8.paperCode = 7;
        items.add(item8);

        YearItem item9 = new YearItem();
        item9.type = YearItem.YEAR_SET;
        item9.title = "Model 3";
        item9.paperCode = 8;
        items.add(item9);

        YearItem item10 = new YearItem();
        item10.type = YearItem.YEAR_SET;
        item10.title = "Model 4";
        item10.paperCode = 9;
        items.add(item10);

        YearItem item11 = new YearItem();
        item11.type = YearItem.YEAR_SET;
        item11.title = "Model 5";
        item11.paperCode = 10;
        items.add(item11);

        YearItem item12 = new YearItem();
        item12.type = YearItem.YEAR_SET;
        item12.title = "Model 6";
        item12.paperCode = 11;
        items.add(item12);
    }

    private void prepareOldQuestions() {
        YearItem item = new YearItem();
        item.type = YearItem.SECTION_TITLE;
        item.title = "Old Questions";
        items.add(item);

        YearItem item1 = new YearItem();
        item1.type = YearItem.YEAR_SET;
        item1.title = "2069 TU Examination";
        item1.paperCode = 0;
        items.add(item1);

        YearItem item2 = new YearItem();
        item2.type = YearItem.YEAR_SET;
        item2.title = "2070 TU Examination";
        item2.paperCode = 1;
        items.add(item2);

        YearItem item3 = new YearItem();
        item3.type = YearItem.YEAR_SET;
        item3.title = "2071 TU Examination";
        item3.paperCode = 2;
        items.add(item3);

        YearItem item4 = new YearItem();
        item4.type = YearItem.YEAR_SET;
        item4.title = "2072 TU Examination";
        item4.paperCode = 3;
        items.add(item4);

        YearItem item5 = new YearItem();
        item5.type = YearItem.YEAR_SET;
        item5.title = "2073 TU Examination";
        item5.paperCode = 4;
        items.add(item5);

        YearItem item6 = new YearItem();
        item6.type = YearItem.YEAR_SET;
        item6.title = "2074 TU Examination";
        item6.paperCode = 5;
        items.add(item6);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_years_list, container, false);
    }
}
