package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.devspark.robototextview.widget.RobotoTextView;

import np.com.aawaz.csitentrance.R;

public class SubjectYearChooser extends BottomSheetDialogFragment {

    public SubjectYearChooser() {
        // Required empty public constructor
    }

    public static SubjectYearChooser newInstance(String subject) {
        SubjectYearChooser fragment = new SubjectYearChooser();
        Bundle args = new Bundle();
        args.putString("subject", subject);
        fragment.setArguments(args);
        return fragment;
    }

    RobotoTextView subjectName, option1, option2, option3, option4, option5, option6, option7, option8;

    public void onViewCreated(View view) {
        option1 = (RobotoTextView) view.findViewById(R.id.quest1);
        option2 = (RobotoTextView) view.findViewById(R.id.quest2);
        option3 = (RobotoTextView) view.findViewById(R.id.quest3);
        option4 = (RobotoTextView) view.findViewById(R.id.quest4);
        option5 = (RobotoTextView) view.findViewById(R.id.quest5);
        option6 = (RobotoTextView) view.findViewById(R.id.quest6);
        option7 = (RobotoTextView) view.findViewById(R.id.quest7);
        option8 = (RobotoTextView) view.findViewById(R.id.quest8);
        subjectName = (RobotoTextView) view.findViewById(R.id.subjectName);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_subject_year_chooser, null);
        dialog.setContentView(contentView);
        onViewCreated(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        subjectName.setText(getArguments().getString("subject"));
    }
}
