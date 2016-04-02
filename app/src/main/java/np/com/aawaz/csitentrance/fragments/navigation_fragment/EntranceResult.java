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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.devspark.robototextview.widget.RobotoTextView;
import com.truizlop.fabreveallayout.FABRevealLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.CustomSlideView;
import np.com.aawaz.csitentrance.misc.SPHandler;
import np.com.aawaz.csitentrance.misc.Singleton;

public class EntranceResult extends Fragment {
    RequestQueue requestQueue;
    FloatingActionButton resultButton;

    ViewSwitcher resultViewSwitcher;
    AppCompatEditText rollNo;
    FABRevealLayout revealLayout;

    ProgressBar progressBarResult;
    RobotoTextView resultHolder;
    ImageView cross;
    SliderLayout adSlider;
    private InputMethodManager imm;


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
        } else {
            isPublishedCheck();
        }

        readyAd();
    }

    private void readyAd() {
        final JsonArrayRequest request = new JsonArrayRequest(getString(R.string.sliderAd), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        final JSONObject object = response.getJSONObject(i);

                        Bundle bundle = new Bundle();
                        bundle.putString("destination_url", object.getString("destination_url"));
                        bundle.putString("description", object.getString("sub_title"));

                        CustomSlideView textSliderView = new CustomSlideView(getContext());
                        // initialize a SliderLayout
                        textSliderView
                                .description(object.getString("title"))
                                .image(object.getString("image_url"))
                                .bundle(bundle)
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                        adSlider.addSlider(textSliderView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Singleton.getInstance().getRequestQueue().add(request);
    }

    private void isPublishedCheck() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .content("Connecting...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, getString(R.string.isPublished), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("published")) {
                    SPHandler.getInstance().setResultPublished();
                    workForViewingResult();
                    resultViewSwitcher.showNext();
                    rollNo.requestFocus();
                    dialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getContext(), "Unable to connect to the server. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }
}
