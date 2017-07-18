package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.app.Dialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.Feedback;
import np.com.aawaz.csitentrance.objects.PopupAd;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class PopupDialogFragment extends DialogFragment {
    ImageView imagePopup;
    TextView title, address, content, call, know, close;
    ViewSwitcher popupViewSwitcher;
    ArrayList<PopupAd> ads = new ArrayList<>();
    boolean isDataShowing = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_dialog, container, false);
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
        FirebaseDatabase.getInstance().getReference().child("ads").child("popup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ads.add(child.getValue(PopupAd.class));
                }
                fillAd();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismiss();
            }
        });


    }

    private void fillAd() {
        //sizing the dialog
        int lastAdPosition = SPHandler.getInstance().getLastAdPosition();
        if (lastAdPosition >= ads.size()) {
            lastAdPosition = 0;
            SPHandler.getInstance().setLastAd(0);
        } else
            SPHandler.getInstance().setLastAd((lastAdPosition + 1) % ads.size());

        final PopupAd adToShow = ads.get(lastAdPosition);

        int image = 0;
        if (adToShow.banner_image.equals("samriddhi"))
            image = R.drawable.samriddhi;
        else if (adToShow.banner_image.equals("sagarmatha"))
            image = R.drawable.sagarmatha;


        new EventSender()
                .logEvent(adToShow.banner_image + "_popup");

        imagePopup.setImageDrawable(ContextCompat.getDrawable(getContext(), image));
        Picasso.with(getContext())
                .load(image)
                .into(imagePopup);

        title.setText(adToShow.title);
        address.setText(adToShow.address);
        content.setText(Html.fromHtml(adToShow.detail));

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("ad_user_data")
                        .child(adToShow.title)
                        .child("students")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Feedback(SPHandler.getInstance().getPhoneNo()));

                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", adToShow.phone, null)));
                dismiss();
            }
        });

        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("ad_user_data")
                        .child(adToShow.title)
                        .child("students")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Feedback(SPHandler.getInstance().getPhoneNo()));

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(adToShow.website)));
                dismiss();
            }
        });

        if (!isDataShowing) {
            popupViewSwitcher.showPrevious();
            isDataShowing = true;
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout((9 * metrics.widthPixels) / 10, (6 * metrics.heightPixels) / 7);
    }
}
