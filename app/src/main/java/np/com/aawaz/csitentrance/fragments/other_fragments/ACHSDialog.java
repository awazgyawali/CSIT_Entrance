package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.Feedback;
import np.com.aawaz.csitentrance.objects.PopupAd;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class ACHSDialog extends DialogFragment {
    ImageView imagePopup;
    TextView title, address, content, call, know, close;
    ViewSwitcher popupViewSwitcher;
    boolean isDataShowing = false;
    private Context context;
    private PopupAd adToShow;

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.achs_popup_dialog, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        popupViewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcherPopup);
        imagePopup = (ImageView) view.findViewById(R.id.image_ad_popup);
        title = (TextView) view.findViewById(R.id.popup_title);
        address = (TextView) view.findViewById(R.id.popup_address);
        content = (TextView) view.findViewById(R.id.popup_desc);
        call = (TextView) view.findViewById(R.id.call_popup);
        know = (TextView) view.findViewById(R.id.popup_knowmore);
        close = (TextView) view.findViewById(R.id.closePopup);
        popupViewSwitcher.showNext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        fillAd();
    }

    public void setAd(final PopupAd adToShow) {
        this.adToShow = adToShow;
    }

    private void fillAd() {
        int image = 0;
        switch (adToShow.getBanner_image()) {
            case "samriddhi":
                image = R.drawable.samriddhi;
                break;
            case "orchid":
                image = R.drawable.orchid_splash;
                break;
        }


        new EventSender()
                .logEvent(adToShow.getBanner_image() + "_popup");

        imagePopup.setImageDrawable(ContextCompat.getDrawable(context, image));
        Picasso.with(context)
                .load(image)
                .into(imagePopup);

        title.setText(adToShow.getTitle());
        address.setText(adToShow.getAddress());
        content.setText(Html.fromHtml(adToShow.getDetail()));

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("ad_user_data")
                        .child(adToShow.getTitle())
                        .child("students")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Feedback(SPHandler.getInstance().getPhoneNo()));

                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", adToShow.getPhone(), null)));
                dismiss();
            }
        });

        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("ad_user_data")
                        .child(adToShow.getTitle())
                        .child("students")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Feedback(SPHandler.getInstance().getPhoneNo()));

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(adToShow.getWebsite())));
                dismiss();
            }
        });

        if (!isDataShowing) {
            popupViewSwitcher.showPrevious();
            isDataShowing = true;
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout((19 * metrics.widthPixels) / 20, (19 * metrics.heightPixels) / 20);
    }
}
