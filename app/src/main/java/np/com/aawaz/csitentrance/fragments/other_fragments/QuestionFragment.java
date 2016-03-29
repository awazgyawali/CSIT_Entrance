package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.devspark.robototextview.widget.RobotoTextView;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.QuizInterface;
import np.com.aawaz.csitentrance.misc.QuizTextView;

public class QuestionFragment extends Fragment implements View.OnClickListener {

    QuizTextView question, option1, option2, option3, option4;
    RobotoTextView questionRo, option1Ro, option2Ro, option3Ro, option4Ro;
    RelativeLayout option1Listener, option2Listener, option3Listener, option4Listener;
    CardView option1Selected, option2Selected, option3Selected, option4Selected;

    CardView submit;

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
        question = (QuizTextView) view.findViewById(R.id.questionWeb);
        option1 = (QuizTextView) view.findViewById(R.id.optionWeb1);
        option2 = (QuizTextView) view.findViewById(R.id.optionWeb2);
        option3 = (QuizTextView) view.findViewById(R.id.optionWeb3);
        option4 = (QuizTextView) view.findViewById(R.id.optionWeb4);

        questionRo = (RobotoTextView) view.findViewById(R.id.questionRobo);
        option1Ro = (RobotoTextView) view.findViewById(R.id.optionRobo1);
        option2Ro = (RobotoTextView) view.findViewById(R.id.optionRobo2);
        option3Ro = (RobotoTextView) view.findViewById(R.id.optionRobo3);
        option4Ro = (RobotoTextView) view.findViewById(R.id.optionRobo4);

        option1Listener = (RelativeLayout) view.findViewById(R.id.option1);
        option2Listener = (RelativeLayout) view.findViewById(R.id.option2);
        option3Listener = (RelativeLayout) view.findViewById(R.id.option3);
        option4Listener = (RelativeLayout) view.findViewById(R.id.option4);


        option1Selected = (CardView) view.findViewById(R.id.optSelected1);
        option2Selected = (CardView) view.findViewById(R.id.optSelected2);
        option3Selected = (CardView) view.findViewById(R.id.optSelected3);
        option4Selected = (CardView) view.findViewById(R.id.optSelected4);

        submit = (CardView) view.findViewById(R.id.AnswerFab);

        YoYo.with(Techniques.SlideOutDown)
                .duration(500)
                .playOn(submit);

        option1Listener.setOnClickListener(this);
        option2Listener.setOnClickListener(this);
        option3Listener.setOnClickListener(this);
        option4Listener.setOnClickListener(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String questionText = getArguments().getString("question");
        final String opt1Text = getArguments().getString("a");
        final String opt2Text = getArguments().getString("b");
        final String opt3Text = getArguments().getString("c");
        final String opt4Text = getArguments().getString("d");

        if (questionText.contains("$")) {
            questionRo.setVisibility(View.GONE);
            question.setVisibility(View.VISIBLE);
            question.initializer();
            question.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    question.setScript(questionText);
                }
            });
        } else {
            question.setVisibility(View.GONE);
            questionRo.setVisibility(View.VISIBLE);
            questionRo.setText(Html.fromHtml(questionText));
        }


        if (opt1Text.contains("$")) {
            option1Ro.setVisibility(View.GONE);
            option1.setVisibility(View.VISIBLE);
            option1.initializer();
            option1.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    option1.setScript(opt1Text);
                }
            });
        } else {
            option1.setVisibility(View.GONE);
            option1Ro.setVisibility(View.VISIBLE);
            option1Ro.setText(Html.fromHtml(opt1Text));
        }

        if (opt2Text.contains("$")) {
            option2Ro.setVisibility(View.GONE);
            option2.setVisibility(View.VISIBLE);
            option2.initializer();
            option2.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    option2.setScript(opt2Text);
                }
            });
        } else {
            option2.setVisibility(View.GONE);
            option2Ro.setVisibility(View.VISIBLE);
            option2Ro.setText(Html.fromHtml(opt2Text));
        }

        if (opt3Text.contains("$")) {
            option3Ro.setVisibility(View.GONE);
            option3.setVisibility(View.VISIBLE);
            option3.initializer();
            option3.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    option3.setScript(opt3Text);
                }
            });
        } else {
            option3.setVisibility(View.GONE);
            option3Ro.setVisibility(View.VISIBLE);
            option3Ro.setText(Html.fromHtml(opt3Text));
        }

        if (opt1Text.contains("$")) {
            option4Ro.setVisibility(View.GONE);
            option4.setVisibility(View.VISIBLE);

            option4.initializer();
            option4.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    option4.setScript(opt4Text);
                }
            });
        } else {
            option4.setVisibility(View.GONE);
            option4Ro.setVisibility(View.VISIBLE);
            option4Ro.setText(Html.fromHtml(opt4Text));
        }


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

    @Override
    public void onClick(View view) {
        if (clickedAns == 0)
            YoYo.with(Techniques.SlideInUp)
                    .duration(500)
                    .playOn(submit);

        option1Selected.setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
        option2Selected.setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
        option3Selected.setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
        option4Selected.setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
        switch (view.getId()) {
            case R.id.option1:
                clickedAns = 1;
                option1Selected.setCardElevation(getResources().getDimension(R.dimen.card_max_elevation));
                break;
            case R.id.option2:
                clickedAns = 2;
                option2Selected.setCardElevation(getResources().getDimension(R.dimen.card_max_elevation));
                break;
            case R.id.option3:
                clickedAns = 3;
                option3Selected.setCardElevation(getResources().getDimension(R.dimen.card_max_elevation));
                break;
            case R.id.option4:
                clickedAns = 4;
                option4Selected.setCardElevation(getResources().getDimension(R.dimen.card_max_elevation));
                break;
        }

    }
}
