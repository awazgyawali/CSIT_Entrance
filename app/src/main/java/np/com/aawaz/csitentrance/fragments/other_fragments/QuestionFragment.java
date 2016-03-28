package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.QuizInterface;
import np.com.aawaz.csitentrance.misc.QuizTextView;

public class QuestionFragment extends Fragment {

    QuizTextView question, option1, option2, option3, option4;

    FloatingActionButton fab;

    int clickedAns = 0;
    private QuizInterface listener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(int position, String question, String a, String b,
                                               String c, String d, String ans) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putString("a", a);
        args.putString("b", b);
        args.putString("c", c);
        args.putString("d", d);
        args.putString("ans", ans);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        question = (QuizTextView) view.findViewById(R.id.question);
        option1 = (QuizTextView) view.findViewById(R.id.option1);
        option2 = (QuizTextView) view.findViewById(R.id.option2);
        option3 = (QuizTextView) view.findViewById(R.id.option3);
        option4 = (QuizTextView) view.findViewById(R.id.option4);

        fab = (FloatingActionButton) view.findViewById(R.id.AnswerFab);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getString("question").contains("$"))
            question.initializer();
        if (getArguments().getString("a").contains("$"))
            option1.initializer();
        if (getArguments().getString("b").contains("$"))
            option2.initializer();
        if (getArguments().getString("c").contains("$"))
            option3.initializer();
        if (getArguments().getString("d").contains("$"))
            option4.initializer();

        question.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                question.setScript(getArguments().getString("question"));
            }
        });
        option1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                option1.setScript(getArguments().getString("a"));
            }
        });
        option2.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                option2.setScript(getArguments().getString("b"));
            }
        });
        option3.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                option3.setScript(getArguments().getString("c"));
            }
        });
        option4.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                option4.setScript(getArguments().getString("d"));
            }
        });
    }


    public void checkAnswer() {
        if ((clickedAns == 1 && getArguments().getString("ans").equals("a")) || (clickedAns == 2 && getArguments().getString("ans").equals("b")) ||
                (clickedAns == 3 && getArguments().getString("ans").equals("c")) || (clickedAns == 4 && getArguments().getString("ans").equals("d"))) {
            listener.selected(true);
        } else {
            listener.selected(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    public QuestionFragment setListener(QuizInterface listener) {
        this.listener = listener;
        return this;
    }
}
