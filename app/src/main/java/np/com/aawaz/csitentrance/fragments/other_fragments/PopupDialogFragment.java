package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import np.com.aawaz.csitentrance.objects.Feedback;
import np.com.aawaz.csitentrance.objects.PopupAd;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class PopupDialogFragment extends DialogFragment {
    ImageView imagePopup;
    TextView title, address, content, call, know, close;
    ViewSwitcher popupViewSwitcher;
    ArrayList<PopupAd> ads = new ArrayList<>();


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
        SPHandler.getInstance().setLastAd((lastAdPosition + 1) % ads.size());

        final PopupAd adToShow = ads.get(lastAdPosition);

        Picasso.with(getContext())
                .load(adToShow.banner_image)
                .into(imagePopup);

        title.setText(adToShow.title);
        address.setText(adToShow.address);
        content.setText(adToShow.detail);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(adToShow.phone), null)));
                dismiss();
            }
        });

        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference()
                        .child("college_feed")
                        .child(adToShow.title)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Feedback(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(adToShow.website)));
                dismiss();
            }
        });


        popupViewSwitcher.showPrevious();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout((9 * metrics.widthPixels) / 10, (9 * metrics.heightPixels) / 10);
    }
}
