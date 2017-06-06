package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;

public class PopupDialogFragment extends DialogFragment {
    CircleImageView imagePopup;
    TextView title, content, call, know, close;
    ViewSwitcher popupViewSwitcher;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_dialog, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupViewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcherPopup);
        close = (TextView) view.findViewById(R.id.closePopup);
        popupViewSwitcher.showNext();
    }

    @Override
    public void onStart() {
        super.onStart();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupViewSwitcher.showPrevious();
            }
        }, 2000);
    }
}
