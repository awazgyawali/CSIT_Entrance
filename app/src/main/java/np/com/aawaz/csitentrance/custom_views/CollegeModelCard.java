package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import np.com.aawaz.csitentrance.R;

public class CollegeModelCard extends RelativeLayout {
    TextView name, play, view, address;
    ImageView call, logo;
    Context context;
    private CollegeCardListener collegeCardListener;

    public CollegeModelCard(@NonNull Context context) {
        super(context);
        initializeViews(context);
    }

    public CollegeModelCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CollegeModelCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.college_model_question, this);
    }

    public CollegeModelCard setTitle(String title) {
        name.setText(title);
        return this;
    }

    public CollegeModelCard setLogo(int drawable) {
        logo.setImageResource(drawable);
        return this;
    }

    public CollegeModelCard setAddress(String addressText) {
        address.setText(addressText);
        return this;
    }

    public CollegeModelCard setPhoneNumber(final String phone) {
        call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title("Call Confirmation")
                        .content("Call " + phone + "?")
                        .positiveText("Call")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
                            }
                        })
                        .show();
            }
        });
        return this;
    }

    public void setOnMenuClickedListener(CollegeCardListener collegeCardListener) {
        this.collegeCardListener = collegeCardListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        name = findViewById(R.id.college_model_text);
        play = findViewById(R.id.college_model_play);
        view = findViewById(R.id.college_model_view);
        address = findViewById(R.id.college_model_address);
        call = findViewById(R.id.college_model_call);
        logo = findViewById(R.id.college_model_logo);

        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collegeCardListener != null)
                    collegeCardListener.onPlayClicked();
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collegeCardListener != null)
                    collegeCardListener.onViewClicked();
            }
        });
    }

    public interface CollegeCardListener {
        void onPlayClicked();

        void onViewClicked();
    }
}
