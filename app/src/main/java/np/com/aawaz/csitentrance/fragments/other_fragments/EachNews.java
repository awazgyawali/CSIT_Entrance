package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.ImageViewActivity;

public class EachNews extends BottomSheetDialogFragment {


    public EachNews() {
        // Required empty public constructor
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
        View contentView = View.inflate(getContext(), R.layout.fragment_each_news, null);
        dialog.setContentView(contentView);
        onViewCreated(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    private void onViewCreated(View contentView) {
        RobotoTextView title = (RobotoTextView) contentView.findViewById(R.id.newsBottomTitle),
                newsDetail = (RobotoTextView) contentView.findViewById(R.id.newsBottomDetail),
                time = (RobotoTextView) contentView.findViewById(R.id.newsBottomTime);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.newsBottomImage);

        final Bundle bundle = getArguments();
        title.setText(bundle.getString("title"));
        newsDetail.setText(Html.fromHtml(bundle.getString("detail")));
        time.setText(bundle.getString("time"));

        Picasso.with(getContext())
                .load(bundle.getString("image_link"))
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ImageViewActivity.class)
                        .putExtra("image_link", bundle.getString("image_link")));
            }
        });
    }
}
