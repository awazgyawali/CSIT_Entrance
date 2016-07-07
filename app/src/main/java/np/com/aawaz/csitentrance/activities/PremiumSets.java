package np.com.aawaz.csitentrance.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.PremiumAdapter;
import np.com.aawaz.csitentrance.fragments.other_fragments.CouponCode;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.interfaces.CouponListener;
import np.com.aawaz.csitentrance.misc.Singleton;
import np.com.aawaz.csitentrance.objects.PremiumSet;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.objects.SetRequest;

public class PremiumSets extends AppCompatActivity implements ClickListener, CouponListener {
    PremiumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_sets);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_premium));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Premium Sets");

        final ProgressBar loading_premium = (ProgressBar) findViewById(R.id.loading_premium);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.premium_sets_recy);
        adapter = new PremiumAdapter(this);
        adapter.setClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("premium_sets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loading_premium.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    adapter.addSet(child.getValue(PremiumSet.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PremiumSets.this, "Unable to connect, check your connection.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void itemClicked(View view, int position) {
        ///todo check if already bought and should goto quiz or the coupon code entry screen
        CouponCode dialogFragment = new CouponCode();
        dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
        dialogFragment.setCouponCallback(this);
    }

    @Override
    public void itemLongClicked(View view, int position) {

    }

    @Override
    public void onCodeEntered(final String set_id, final String coupon_code) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Validating")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();

        StringRequest request = new StringRequest(getString(R.string.couponValidateUrl), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.toLowerCase().contains("true")) {
                    dialog.dismiss();
                    SPHandler.getInstance().setBoolean(set_id, true);
                    new MaterialDialog.Builder(PremiumSets.this)
                            .title("Successful")
                            .content("You successfully bought the set, now you can access to this set whenever you like.")
                            .positiveText("Got it")
                            .show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(PremiumSets.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(PremiumSets.this, "Unable to connect, check your connection.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", coupon_code);
                params.put("set_id", set_id);
                params.put("uuid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                return params;
            }
        };
        Singleton.getInstance().getRequestQueue().add(request);
    }

    @Override
    public void onRequestEntered() {
        new MaterialDialog.Builder(this)
                .title("Request added")
                .content("Request has been added, we will contact you as soon as possible.\nMake sure that internet is connected to send the request.")
                .positiveText("Ok")
                .show();
        FirebaseDatabase.getInstance().getReference().child("requests").push().setValue(new SetRequest().toMap());
    }
}
