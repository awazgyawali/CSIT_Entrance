package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;

public class YearCard extends RelativeLayout {
    TextView name, play, view;
    ImageView code;
    Context context;
    private YearCardListener collegeCardListener;

    public YearCard(@NonNull Context context) {
        super(context);
        initializeViews(context);
    }

    public YearCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public YearCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.each_year_quiz_card, this);
    }

    public YearCard setTitle(String title) {
        name.setText(title);
        return this;
    }

    public YearCard setCode(String code_string) {
        return this;
    }

    public void setOnMenuClickedListener(YearCardListener collegeCardListener) {
        this.collegeCardListener = collegeCardListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        name = findViewById(R.id.year_text);
        code = findViewById(R.id.code_name);
        play = findViewById(R.id.year_play);
        view = findViewById(R.id.year_view);
        getRootView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collegeCardListener != null)
                    collegeCardListener.onPlayClicked();
            }
        });

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

    public interface YearCardListener {
        void onPlayClicked();

        void onViewClicked();
    }
}
