package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.devspark.robototextview.widget.RobotoTextView;

import np.com.aawaz.csitentrance.R;

public class CustomSlideView extends BaseSliderView {
    private final Context context;

    public CustomSlideView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_slide, null);

        ImageView target = (ImageView) v.findViewById(R.id.image_ad);
        RobotoTextView description = (RobotoTextView) v.findViewById(R.id.description);
        RobotoTextView title = (RobotoTextView) v.findViewById(R.id.title);
        CardView button = (CardView) v.findViewById(R.id.card_ad);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getBundle().getString("destination_url"))));
            }
        });

        title.setText(getDescription());
        description.setText(getBundle().getString("description"));
        bindEventAndShow(v, target);
        return v;
    }
}
