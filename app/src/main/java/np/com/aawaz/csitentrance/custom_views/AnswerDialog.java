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

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.OnDismissListener;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class AnswerDialog extends DialogFragment {

    private String answer = "test";
    private SwitchCompat answer_settings;
    private TextView answerText;
    private QuizTextView answerWeb;
    private OnDismissListener onDismissListener;


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

        answer_settings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHandler.getInstance().showAnswer(isChecked);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        answer_settings = (SwitchCompat) view.findViewById(R.id.answerSwitch);
        answerText = (TextView) view.findViewById(R.id.answerText);
        answerWeb = (QuizTextView) view.findViewById(R.id.answerWeb);
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
