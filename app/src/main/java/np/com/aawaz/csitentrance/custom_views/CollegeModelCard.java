package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;

public class CollegeModelCard extends RelativeLayout {
    TextView name, play, view, address;
    ImageView logo;
    Context context;

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        name = findViewById(R.id.title);
        address = findViewById(R.id.address);
        logo = findViewById(R.id.code_name);

    }
}
