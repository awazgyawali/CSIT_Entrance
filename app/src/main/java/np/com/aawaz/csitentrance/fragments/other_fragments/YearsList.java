package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.FullQuestionActivity;
import np.com.aawaz.csitentrance.activities.YearQuizActivity;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class YearsList extends Fragment {

    public YearsList() {
        // Required empty public constructor
    }

    public static YearsList newInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        YearsList list = new YearsList();
        list.setArguments(bundle);
        return list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout que69, que70, que71, que72, que73, que1, que2, que3, que4, que5, que6;

        que1 = (LinearLayout) view.findViewById(R.id.question1);
        que2 = (LinearLayout) view.findViewById(R.id.question2);
        que3 = (LinearLayout) view.findViewById(R.id.question3);
        que4 = (LinearLayout) view.findViewById(R.id.question4);
        que5 = (LinearLayout) view.findViewById(R.id.question5);
        que6 = (LinearLayout) view.findViewById(R.id.question6);
        que69 = (LinearLayout) view.findViewById(R.id.question2069);
        que70 = (LinearLayout) view.findViewById(R.id.question2070);
        que71 = (LinearLayout) view.findViewById(R.id.question2071);
        que72 = (LinearLayout) view.findViewById(R.id.question2072);
        que73 = (LinearLayout) view.findViewById(R.id.question2073);

        que69.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(0);
            }
        });
        que70.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(1);
            }
        });
        que71.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(2);
            }
        });
        que72.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(3);
            }
        });
        que73.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(8);
            }
        });//assiginig 8 to 2073 question
        que1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(4);
            }
        });
        que2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(5);
            }
        });
        que3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(6);
            }
        });
        que4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(7);
            }
        });
        que5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(9);
            }
        });
        que6.setVisibility(View.GONE);//todo to be removed
        que6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(10);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_years_list, container, false);
    }

    private void clicked(int position) {
        if (getArguments().getString("type").equals("full")) {
            openFullQuestion(position);
        } else {
            openQuizQuestion(position);
        }
    }

    public void openFullQuestion(int position) {
        String[] codes = {SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072,
                SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4, SPHandler.YEAR2073, SPHandler.MODEL5, SPHandler.MODEL6};
        Intent intent = new Intent(getContext(), FullQuestionActivity.class);
        intent.putExtra("code", codes[position]);
        intent.putExtra("position", position + 1);
        new EventSender().logEvent("viewed_full");

        startActivity(intent);
    }

    public void openQuizQuestion(int position) {
        String[] codes = {SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072,
                SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4, SPHandler.YEAR2073, SPHandler.MODEL5, SPHandler.MODEL6};
        Intent intent = new Intent(getContext(), YearQuizActivity.class);

        intent.putExtra("code", codes[position]);
        intent.putExtra("position", position);
        new EventSender().logEvent("played_year");

        startActivity(intent);
    }
}
