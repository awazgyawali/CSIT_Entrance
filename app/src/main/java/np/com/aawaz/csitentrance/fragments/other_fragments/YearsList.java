package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.QuizActivity;
import np.com.aawaz.csitentrance.activities.WebViewActivity;

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
        CardView que69, que70, que71, que72, que1, que2, que3, que4;

        que1 = (CardView) view.findViewById(R.id.question1);
        que2 = (CardView) view.findViewById(R.id.question2);
        que3 = (CardView) view.findViewById(R.id.question3);
        que4 = (CardView) view.findViewById(R.id.question4);
        que69 = (CardView) view.findViewById(R.id.question2069);
        que70 = (CardView) view.findViewById(R.id.question2070);
        que71 = (CardView) view.findViewById(R.id.question2071);
        que72 = (CardView) view.findViewById(R.id.question2072);

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
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("code", position + 1);
        startActivity(intent);
    }

    public void openQuizQuestion(int position) {
        Intent intent = new Intent(getContext(), QuizActivity.class);
        intent.putExtra("position", position + 1);
        startActivity(intent);
    }
}
