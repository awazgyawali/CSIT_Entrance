package np.com.aawaz.csitentrance;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.mrengineer13.snackbar.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class CSITQuery extends AppCompatActivity {

    Button send;
    EditText text;
    RecyclerView recyclerView;
    QueryAdapter adapter;
    RequestQueue queue;

    ArrayList<Integer> flag = new ArrayList<>();
    ArrayList<String> messages = new ArrayList<>();
    CallbackManager callbackManager;
    LoginButton button;
    SharedPreferences pref;
    String id;
    Context context;
    private ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csitquery);
        setSupportActionBar((Toolbar) findViewById(R.id.queryToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pref = getSharedPreferences("details", MODE_PRIVATE);
        id = pref.getString("fbId", "0");
        queue = VolleySingleton.getInstance().getRequestQueue();

        context = this;

        send = (Button) findViewById(R.id.sendBtn);
        text = (EditText) findViewById(R.id.text);
        recyclerView = (RecyclerView) findViewById(R.id.recyQuery);
        if (id.equals("0")) {
            onStartHandler();
        } else {
            MaterialDialog dialogInitial = new MaterialDialog.Builder(this)
                    .content("Please wait...")
                    .progress(true, 0)
                    .build();
            dialogInitial.show();
            fetchFromInternet(dialogInitial, true);
        }
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void fetchFromInternet(final MaterialDialog initial, final boolean finish) {
        String url = getString(R.string.queryFetchUrl);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray query = response.getJSONArray("query");
                    messages.clear();
                    flag.clear();
                    for (int i = 0; i < query.length(); i++) {
                        JSONObject jo_inside = query.getJSONObject(i);
                        if (!jo_inside.getString("Question").equals("")) {
                            messages.add(jo_inside.getString("Question"));
                            flag.add(0);
                        }
                        if (!jo_inside.getString("Answer").equals("")) {
                            messages.add(jo_inside.getString("Answer"));
                            flag.add(1);
                        }
                        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("query", messages.size()).apply();
                    }
                    adapter = new QueryAdapter(context, messages, flag);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(messages.size());
                    initial.dismiss();
                } catch (JSONException e) {
                    if (finish)
                        finish();
                    Toast.makeText(context, "Application error. Please report us.", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (finish)
                    finish();
                Toast.makeText(context, "Server down. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void onStartHandler() {
        //Initialize Facebook SDK
        FacebookSdk.sdkInitialize(context);

        button = new LoginButton(this);


        //set fb permissions
        button.setReadPermissions(Arrays.asList("public_profile,email"));

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(button, false)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                })
                .show();

        //call the login callback manager
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("fbId", profile2.getId() + "");
                                editor.putString("First", profile2.getName() + "");
                                editor.apply();
                                dialog.dismiss();
                                mProfileTracker.stopTracking();
                            }
                        };
                        mProfileTracker.startTracking();
                        fetchFromInternet(new MaterialDialog.Builder(context).build(), false);

                    }

                    @Override
                    public void onCancel() {
                        finish();
                    }

                    @Override
                    public void onError(FacebookException e) {
                        finish();
                    }


                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendSms(View v) {
        String url = getString(R.string.quertyPostUrl);
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();
        Uri.Builder uri = new Uri.Builder();
        String values = uri.authority("")
                .appendQueryParameter("Question", text.getText().toString().replace("\"", "").replace("}", "").replace("{", "").replace(",", ""))
                .appendQueryParameter("fbId", pref.getString("fbId", ""))
                .appendQueryParameter("Name", pref.getString("First", ""))
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + values, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    response.getString("feedBack");
                    adapter.added(text.getText().toString());
                    text.setText("");
                    new SnackBar.Builder((Activity) context)
                            .withMessage("Question posted successfully. We will reply you soon.")
                            .show();
                    getSharedPreferences("data", MODE_PRIVATE).edit().putInt("query", getSharedPreferences("data", MODE_PRIVATE).getInt("query", 1) + 1).apply();
                } catch (JSONException e) {
                    new SnackBar.Builder((Activity) context)
                            .withMessage("Application Error. Please report us.")
                            .show();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                new SnackBar.Builder((Activity) context)
                        .withMessage("Seems server is currently down. Please try again later.")
                        .show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
