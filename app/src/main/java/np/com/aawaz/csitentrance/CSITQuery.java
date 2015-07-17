package np.com.aawaz.csitentrance;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
        queue = Volley.newRequestQueue(this);


        send = (Button) findViewById(R.id.sendBtn);
        text = (EditText) findViewById(R.id.text);
        recyclerView = (RecyclerView) findViewById(R.id.recyQuery);
        if (id.equals("0")) {
            onStartHandler();
        } else {
            intialFetchFromDb();
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

    private void intialFetchFromDb() {
        NewsDataBase databaseClass = new NewsDataBase(this);
        SQLiteDatabase database = databaseClass.getWritableDatabase();
        Cursor cursor = database.query("query", new String[]{"flag,Question,Answer"}, null, null, null, null, null);
        messages.clear();
        flag.clear();
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("flag")) == 0) {
                messages.add(cursor.getString(cursor.getColumnIndex("Question")));
                flag.add(0);
            } else {
                messages.add(cursor.getString(cursor.getColumnIndex("Answer")));
                flag.add(1);
            }
        }

        adapter = new QueryAdapter(getApplicationContext(), messages, flag);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messages.size());

        cursor.close();
        database.close();

        fetchFromInternet();
    }

    private void fetchFromInternet() {
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
                    }
                    storeToDb();
                    adapter = new QueryAdapter(getApplicationContext(), messages, flag);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(messages.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    private void storeToDb() {
        NewsDataBase dataBase = new NewsDataBase(this);
        SQLiteDatabase database = dataBase.getWritableDatabase();
        database.delete("query", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < messages.size(); i++) {
            values.clear();
            if (flag.get(i) == 0) {
                values.put("Question", messages.get(i));
                values.put("flag", flag.get(i));
            } else {
                values.put("Answer", messages.get(i));
                values.put("flag", flag.get(i));
            }
            database.insert("query", null, values);
        }
        database.close();
    }

    private void onStartHandler() {
        //Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

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
                        fetchFromInternet();

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
        Uri.Builder uri = new Uri.Builder();
        String values = uri.authority("")
                .appendQueryParameter("Question", text.getText().toString().replace("\"", "").replace("}", "").replace("{", "").replace(",", ""))
                .appendQueryParameter("fbId", pref.getString("fbId", ""))
                .appendQueryParameter("Name", pref.getString("First", ""))
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + values, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                adapter.added(text.getText().toString());
                text.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }


}
