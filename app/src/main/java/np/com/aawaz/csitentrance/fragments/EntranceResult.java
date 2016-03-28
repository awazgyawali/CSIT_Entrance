package np.com.aawaz.csitentrance.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.volley.toolbox.StringRequest;
import com.devspark.robototextview.widget.RobotoTextView;
import com.truizlop.fabreveallayout.FABRevealLayout;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.Singleton;

public class EntranceResult extends Fragment {
    RequestQueue requestQueue;
    SharedPreferences result;
    FloatingActionButton resultButton;

    ViewSwitcher resultViewSwitcher;
    AppCompatEditText rollNo;
    FABRevealLayout revealLayout;

    ProgressBar progressBarResult;
    RobotoTextView resultHolder;
    ImageView cross;


    private void workForViewingResult() {
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                revealLayout.revealSecondaryView();
                progressBarResult.setVisibility(View.VISIBLE);
                resultHolder.setVisibility(View.GONE);
                StringRequest request = new StringRequest(Request.Method.GET, "http://avaaj.com.np/jsonFeed/result.php?rollNo=" + rollNo.getText(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        resultHolder.setVisibility(View.VISIBLE);
                        progressBarResult.setVisibility(View.GONE);
                        if (response.contains("null")) {
                            resultHolder.setText("Sorry!! This roll number is not in the list.");
                            waitFor(10000);
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
                        waitFor(3000);
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

        result = getContext().getSharedPreferences("resultInfo", Context.MODE_PRIVATE);

        if (result.getBoolean("published", false)) {
            resultViewSwitcher.showNext();
            rollNo.requestFocus();
            workForViewingResult();
        } else {
            isPublishedCheck();
        }
    }

    private void isPublishedCheck() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .content("Connecting...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, "http://avaaj.com.np/jsonFeed/isPublished.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("published")) {
                    result.edit().putBoolean("published", true).apply();
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

    public void waitFor(final long mils) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                revealLayout.revealMainView();
            }
        }, mils);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }
}
