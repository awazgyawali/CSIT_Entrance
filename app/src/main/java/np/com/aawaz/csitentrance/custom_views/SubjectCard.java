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

public class SubjectCard extends RelativeLayout {

    private ImageView image;
    private TextView name;

    public SubjectCard(@NonNull Context context) {
        super(context);
        initializeViews(context);
    }

    public SubjectCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SubjectCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.subject_card, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        image = findViewById(R.id.subject_image);
        name = findViewById(R.id.subject_name);

    }

    public SubjectCard setImage(int resource) {
        image.setImageResource(resource);
        return this;
    }

    public SubjectCard setText(String str_name) {
        name.setText(str_name);
        return this;
    }


}
