package np.com.aawaz.csitentrance.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.QuizInterface;
import np.com.aawaz.csitentrance.misc.CustomWebView;

public class QuestionFragment extends Fragment {

    CustomWebView question, option1, option2, option3, option4;
    AppCompatCheckBox opt1, opt2, opt3, opt4;
    LinearLayout optionHolder1, optionHolder2, optionHolder3, optionHolder4;


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
        question = (CustomWebView) view.findViewById(R.id.question);
        option1 = (CustomWebView) view.findViewById(R.id.option1);
        option2 = (CustomWebView) view.findViewById(R.id.option2);
        option3 = (CustomWebView) view.findViewById(R.id.option3);
        option4 = (CustomWebView) view.findViewById(R.id.option4);

        opt1 = (AppCompatCheckBox) view.findViewById(R.id.check1);
        opt2 = (AppCompatCheckBox) view.findViewById(R.id.check2);
        opt3 = (AppCompatCheckBox) view.findViewById(R.id.check3);
        opt4 = (AppCompatCheckBox) view.findViewById(R.id.check4);

        optionHolder1 = (LinearLayout) view.findViewById(R.id.optionHolder1);
        optionHolder2 = (LinearLayout) view.findViewById(R.id.optionHolder2);
        optionHolder3 = (LinearLayout) view.findViewById(R.id.optionHolder3);
        optionHolder4 = (LinearLayout) view.findViewById(R.id.optionHolder4);

        fab = (FloatingActionButton) view.findViewById(R.id.AnswerFab);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        onClickOption();
    }

    private void onClickOption() {
        optionHolder1.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        op1Click();
                        return true;
                    }
                }
        );
        optionHolder2.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        op2Click();
                        return true;
                    }
                }
        );
        optionHolder3.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        op3Click();
                        return true;
                    }
                }
        );
        optionHolder4.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        op4Click();
                        return true;
                    }
                }
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
                //todo fab invisible
            }


        });
    }


    private void op1Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            fab.show();
        }
        resetBackground();
        opt1.setChecked(true);
        clickedAns = 1;
    }

    private void resetBackground() {
        opt1.setChecked(false);
        opt2.setChecked(false);
        opt3.setChecked(false);
        opt4.setChecked(false);
    }

    private void op2Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            fab.show();
        }
        resetBackground();
        opt2.setChecked(true);
        clickedAns = 2;
    }

    private void op3Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            fab.show();
        }
        resetBackground();
        opt3.setChecked(true);
        clickedAns = 3;
    }

    private void op4Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            fab.show();
        }
        opt4.setChecked(true);
        clickedAns = 4;
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
