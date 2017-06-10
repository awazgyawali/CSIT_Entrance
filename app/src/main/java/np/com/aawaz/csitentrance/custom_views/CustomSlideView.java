package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

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
        TextView description = (TextView) v.findViewById(R.id.description);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView address = (TextView) v.findViewById(R.id.address_slide);
        TextView call = (TextView) v.findViewById(R.id.call_result);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(getBundle().getLong("phone_no")), null)));
            }
        });

        title.setText(getDescription());
        description.setText(getBundle().getString("description"));
        address.setText(getBundle().getString("address"));
        bindEventAndShow(v, target);
        return v;
    }
}
