package np.com.aawaz.csitentrance.custom_views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.database.FirebaseDatabase;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.OnDismissListener;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class AnswerDialog extends DialogFragment {

    private String answer = "test";
    private SwitchCompat answer_settings;
    private TextView answerText, answerIsWrong;
    private QuizTextView answerWeb;
    private OnDismissListener onDismissListener;
    private FancyButton optA, optB, optC, optD, optElse;
    private ViewSwitcher recommender;

    public AnswerDialog() {
        // Required empty public constructor
    }

    public static AnswerDialog newInstance(String answer) {
        AnswerDialog fragment = new AnswerDialog();
        Bundle args = new Bundle();
        args.putString("answer", answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            answer = getArguments().getString("answer");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (answer.contains("<img")) {
            answerText.setVisibility(View.GONE);
            answerWeb.setVisibility(View.VISIBLE);
            answerWeb.setScript(answer);
        } else {
            answerWeb.setVisibility(View.GONE);
            answerText.setVisibility(View.VISIBLE);
            answerText.setText(Html.fromHtml(answer));
        }

        recommenderCode();


        answer_settings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHandler.getInstance().showAnswer(isChecked);
            }
        });
    }

    private void recommenderCode() {
        answerIsWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommender.showNext();
            }
        });

        optA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("report").child("year_question").child(SPHandler.getInstance().getCurrentUid()).setValue("Something else");
                Toast.makeText(getContext(), "Thanks for the report.", Toast.LENGTH_SHORT).show();
                recommender.setVisibility(View.GONE);
            }
        });

        optB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("report").child("year_question").child(SPHandler.getInstance().getCurrentUid()).setValue("Something else");
                Toast.makeText(getContext(), "Thanks for the report.", Toast.LENGTH_SHORT).show();
                recommender.setVisibility(View.GONE);
            }
        });

        optC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("report").child("year_question").child(SPHandler.getInstance().getCurrentUid()).setValue("Something else");
                Toast.makeText(getContext(), "Thanks for the report.", Toast.LENGTH_SHORT).show();
                recommender.setVisibility(View.GONE);
            }
        });

        optD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("report").child("year_question").child(SPHandler.getInstance().getCurrentUid()).setValue("Something else");
                Toast.makeText(getContext(), "Thanks for the report.", Toast.LENGTH_SHORT).show();
                recommender.setVisibility(View.GONE);
            }
        });

        optElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("report").child("year_question").child(SPHandler.getInstance().getCurrentUid()).setValue("Something else");
                Toast.makeText(getContext(), "Thanks for the report.", Toast.LENGTH_SHORT).show();
                recommender.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        answer_settings = (SwitchCompat) view.findViewById(R.id.answerSwitch);
        answerText = (TextView) view.findViewById(R.id.answerText);
        answerWeb = (QuizTextView) view.findViewById(R.id.answerWeb);
        recommender = (ViewSwitcher) view.findViewById(R.id.answerValidationSwitcher);
        answerIsWrong = (TextView) view.findViewById(R.id.recomend_button);
        optA = (FancyButton) view.findViewById(R.id.shouldBeA);
        optB = (FancyButton) view.findViewById(R.id.shouldBeB);
        optC = (FancyButton) view.findViewById(R.id.shouldBeC);
        optD = (FancyButton) view.findViewById(R.id.shouldBeD);
        optElse = (FancyButton) view.findViewById(R.id.shouldBeSomethingElse);
        recommender.setVisibility(View.GONE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDismissListener.onDismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_answer_dialog, container, false);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
