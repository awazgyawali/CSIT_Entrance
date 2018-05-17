package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import np.com.aawaz.csitentrance.R;

public class CollegeAdCard extends RelativeLayout {

    Context context;
    TextView name, address, info;
    ImageView call, logo;

    public CollegeAdCard(Context context) {
        super(context);
        initializeViews(context);
    }

    public CollegeAdCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CollegeAdCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.achs_ad_layout, this);
    }

    public CollegeAdCard setName(String nameString) {
        name.setText(nameString);
        return this;
    }

    public CollegeAdCard setAddress(String addressString) {
        address.setText(addressString);
        return this;
    }

    public CollegeAdCard setSpecialMessage(String messageString) {
        info.setText(messageString);
        return this;
    }

    public CollegeAdCard setPhoneNumber(final String phone) {
        call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        return this;
    }

    public CollegeAdCard setLogo(int logoResource) {
        logo.setImageResource(logoResource);
        return this;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        name = findViewById(R.id.collegeName);
        address = findViewById(R.id.collegeAddress);
        info = findViewById(R.id.specialMessage);
        call = findViewById(R.id.callCollege);
        logo = findViewById(R.id.collegeImage);
    }
}
