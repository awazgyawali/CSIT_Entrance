package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.CouponListener;

public class CouponCode extends BottomSheetDialogFragment {
    AppCompatEditText code;
    Button request;
    ImageView codeButton;


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
    private CouponListener couponCallback;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.coupon_code, null);
        dialog.setContentView(contentView);

        fetchViews(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetBehavior) behavior);
            bottomSheetBehavior.setPeekHeight(1000);
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    private void fetchViews(View contentView) {
        code = (AppCompatEditText) contentView.findViewById(R.id.code_text);
        codeButton = (ImageView) contentView.findViewById(R.id.sendCode);
        request = (Button) contentView.findViewById(R.id.request_code);
        code.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() >= 10) {
                            codeButton.setVisibility(View.VISIBLE);
                        } else {
                            codeButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }

        );
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponCallback.onCodeEntered(getArguments().getString("set_id"), code.getText().toString());
                dismiss();
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponCallback.onRequestEntered();
                dismiss();
            }
        });
    }

    public void setCouponCallback(CouponListener couponCallback) {
        this.couponCallback = couponCallback;
    }
}
