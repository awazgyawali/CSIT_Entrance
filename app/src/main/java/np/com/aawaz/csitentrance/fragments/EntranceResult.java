package np.com.aawaz.csitentrance.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.ViewResult;
import np.com.aawaz.csitentrance.misc.Singleton;

public class EntranceResult extends Fragment {
    TextView unPublished;
    RequestQueue requestQueue;
    MaterialDialog dialog;
    SharedPreferences result;
    FancyButton resultButton;
    LinearLayout viewResult;
    EditText rollNo;
    ImageView adView;


    private void workForViewingResult() {
        resultButton.setEnabled(false);
        rollNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() != 0)
                    resultButton.setEnabled(true);
                else
                    resultButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                StringRequest request = new StringRequest(Request.Method.GET, "http://avaaj.com.np/jsonFeed/result.php?rollNo=" + rollNo.getText(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("null")) {
                            new MaterialDialog.Builder(getContext())
                                    .content("Oops!! This roll number is not in the list.")
                                    .build()
                                    .show();
                        } else {
                            startActivity(new Intent(getContext(), ViewResult.class).putExtra("result", response));
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Snackbar.make(view.findViewById(R.id.coreResult), "Unable to connect to the server. Please try again.", Snackbar.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                requestQueue.add(request);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unPublished = (TextView) view.findViewById(R.id.unPublished);
        viewResult = (LinearLayout) view.findViewById(R.id.viewResult);
        adView = (ImageView) view.findViewById(R.id.adImage);
        resultButton = (FancyButton) view.findViewById(R.id.resultReqButton);
        rollNo = (EditText) view.findViewById(R.id.resultRollNo);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestQueue = Singleton.getInstance().getRequestQueue();
        Picasso.with(getContext())
                .load("www.avaaj.com.np/ads/featured.jpg")
                .into(adView);
        readyDialog();

        result = getContext().getSharedPreferences("resultInfo", Context.MODE_PRIVATE);

        if (result.getBoolean("published", false)) {
            viewResult.setVisibility(View.VISIBLE);
            workForViewingResult();
        } else {
            dialog.show();
            isPublishedCheck();
        }
    }

    private void isPublishedCheck() {
        StringRequest request = new StringRequest(Request.Method.GET, "http://avaaj.com.np/jsonFeed/isPublished.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                if (response.contains("published")) {
                    result.edit().putBoolean("published", true).apply();
                    workForViewingResult();
                    viewResult.setVisibility(View.VISIBLE);
                } else
                    unPublished.setVisibility(View.VISIBLE);

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

    private void readyDialog() {
        dialog = new MaterialDialog.Builder(getContext())
                .progress(true, 0)
                .content("Please wait...")
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result,container,false);
    }
}
