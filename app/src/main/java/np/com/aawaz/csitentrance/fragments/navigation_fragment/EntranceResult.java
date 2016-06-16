package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Context;
import android.net.Uri;
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
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.truizlop.fabreveallayout.FABRevealLayout;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.CustomSlideView;
import np.com.aawaz.csitentrance.misc.Singleton;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class EntranceResult extends Fragment {
    RequestQueue requestQueue;
    FloatingActionButton resultButton;

    ViewSwitcher resultViewSwitcher;
    AppCompatEditText rollNo;
    FABRevealLayout revealLayout;

    ProgressBar progressBarResult, adLoading;
    RobotoTextView resultHolder;
    ImageView cross;
    SliderLayout adSlider;
    private InputMethodManager imm;


    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;

    private void workForViewingResult() {
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                revealLayout.revealSecondaryView();
                imm.hideSoftInputFromWindow(rollNo.getWindowToken(), 0);
                progressBarResult.setVisibility(View.VISIBLE);
                resultHolder.setVisibility(View.GONE);
                StringRequest request = new StringRequest(Request.Method.GET, getString(R.string.viewResult) + rollNo.getText(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        resultHolder.setVisibility(View.VISIBLE);
                        progressBarResult.setVisibility(View.GONE);
                        if (response.contains("null")) {
                            resultHolder.setText("Sorry!! This roll number is not in the list.");
                        } else {
                            resultHolder.setText(Html.fromHtml(response));
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resultHolder.setText("Unable to connect to the server.\n Please try again.");
                        resultHolder.setVisibility(View.VISIBLE);
                        progressBarResult.setVisibility(View.GONE);
                    }
                });
                requestQueue.add(request);
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
        resultHolder = (RobotoTextView) view.findViewById(R.id.resultHolder);
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
        requestQueue = Singleton.getInstance().getRequestQueue();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (SPHandler.getInstance().isResultPublished()) {
            resultViewSwitcher.showNext();
            rollNo.requestFocus();
            workForViewingResult();
        }
        readyAd();
    }

    private void appIndexing() {
        mClient = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        mUrl = Uri.parse("http://csitentrance.brainants.com/result");
        mTitle = "BSc CSIT Entrance Exam Result";
        mDescription = "View entrance result of 2073.";
    }


    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(mUrl)
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getAction());
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end(mClient, getAction());
        mClient.disconnect();
        super.onStop();
    }

    private void readyAd() {
        FirebaseDatabase.getInstance().getReference().child("result_ad").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    adLoading.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putString("destination_url", child.child("destination_url").getValue(String.class));
                    bundle.putString("description", child.child("sub_title").getValue(String.class));

                    CustomSlideView textSliderView = new CustomSlideView(getContext());
                    // initialize a SliderLayout
                    textSliderView
                            .description(child.child("title").getValue(String.class))
                            .image(child.child("image_url").getValue(String.class))
                            .bundle(bundle)
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                    adSlider.addSlider(textSliderView);
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
