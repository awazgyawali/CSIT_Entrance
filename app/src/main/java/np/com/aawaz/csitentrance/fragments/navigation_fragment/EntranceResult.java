package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.truizlop.fabreveallayout.FABRevealLayout;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.ResultSlideView;
import np.com.aawaz.csitentrance.interfaces.ResponseListener;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.services.NetworkRequester;

public class EntranceResult extends Fragment {
    FloatingActionButton resultButton;

    ViewSwitcher resultViewSwitcher;
    AppCompatEditText rollNo;
    FABRevealLayout revealLayout;

    ProgressBar progressBarResult, adLoading;
    TextView resultHolder;
    ImageView cross;
    SliderLayout adSlider;
    private InputMethodManager imm;


    private String mUrl;
    private String mTitle;

    private void workForViewingResult() {
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                revealLayout.revealSecondaryView();
                imm.hideSoftInputFromWindow(rollNo.getWindowToken(), 0);
                progressBarResult.setVisibility(View.VISIBLE);
                resultHolder.setVisibility(View.GONE);

                new NetworkRequester(getString(R.string.viewResult) + rollNo.getText(), new ResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        resultHolder.setVisibility(View.VISIBLE);
                        progressBarResult.setVisibility(View.GONE);
                        if (response.contains("null")) {
                            resultHolder.setText("Sorry!! This roll number is not in the list.");
                        } else {
                            resultHolder.setText(Html.fromHtml(response));
                        }
                    }

                    @Override
                    public void onFailure() {
                        resultHolder.setText("Unable to connect to the server.\n Please try again.");
                        resultHolder.setVisibility(View.VISIBLE);
                        progressBarResult.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resultViewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcherResult);
        resultButton = (FloatingActionButton) view.findViewById(R.id.resultReqButton);
        rollNo = (AppCompatEditText) view.findViewById(R.id.resultRollNo);
        revealLayout = (FABRevealLayout) view.findViewById(R.id.fab_reveal_layout);
        resultHolder = (TextView) view.findViewById(R.id.resultHolder);
        progressBarResult = (ProgressBar) view.findViewById(R.id.progressBarResult);
        adLoading = (ProgressBar) view.findViewById(R.id.addLoading);
        cross = (ImageView) view.findViewById(R.id.crossResult);
        adSlider = (SliderLayout) view.findViewById(R.id.result_ad_slider);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revealLayout.revealMainView();
            }
        });

        rollNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    resultButton.hide();
                else
                    resultButton.show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        resultButton.hide();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (SPHandler.getInstance().isResultPublished()) {
            handlePublished();
        } else {
            FirebaseDatabase.getInstance().getReference().child("result_publish").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(boolean.class)) {
                        handlePublished();
                        SPHandler.getInstance().setResultPublished();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        readyAd();
    }

    private void handlePublished() {
        resultViewSwitcher.showNext();
        rollNo.requestFocus();
        workForViewingResult();
    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/result";
        mTitle = "BSc CSIT Entrance Exam Result";
    }

    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    private void readyAd() {
        FirebaseDatabase.getInstance().getReference().child("ads").child("entrance_result").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    adLoading.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putString("address", child.child("address").getValue(String.class));
                    bundle.putString("button_text", child.child("button_text").getValue(String.class));
                    bundle.putString("destination_url", child.child("destination_url").getValue(String.class));
                    bundle.putString("description", child.child("sub_title").getValue(String.class));

                    ResultSlideView textSliderView = new ResultSlideView(getContext());
                    // initialize a SliderLayout
                    textSliderView
                            .description(child.child("title").getValue(String.class))
                            .image(child.child("image_url").getValue(String.class))
                            .bundle(bundle)
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                    adSlider.addSlider(textSliderView);
                    adSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appIndexing();
        return inflater.inflate(R.layout.fragment_result, container, false);
    }
}
