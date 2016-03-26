package np.com.aawaz.csitentrance.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.QuizActivity;
import np.com.aawaz.csitentrance.activities.WebViewActivity;

public class Home extends Fragment {


    public Home() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //todo full question ani quiz yei bata access dine system ani progress pani
    }

    public void openFullQUestion(int position) {
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("code", position + 1);
        startActivity(intent);
    }

    public void openQuizQUestion(int position) {
        Intent intent = new Intent(getContext(), QuizActivity.class);
        intent.putExtra("position", position + 1);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button) view.findViewById(R.id.openQuiz);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQuizQUestion(0);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
