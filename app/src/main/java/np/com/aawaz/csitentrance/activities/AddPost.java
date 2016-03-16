package np.com.aawaz.csitentrance.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.Singleton;

public class AddPost extends AppCompatActivity {
    AppCompatEditText postText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        setTitle("Post");


        setSupportActionBar((Toolbar) findViewById(R.id.toolbarAddPost));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CircleImageView profPic = (CircleImageView) findViewById(R.id.user_profpic);
        postText = (AppCompatEditText) findViewById(R.id.userPostText);
        final FancyButton postButton = (FancyButton) findViewById(R.id.user_post_button);

        postText.setText(getSharedPreferences("data", MODE_PRIVATE).getString("forumMessage", ""));

        Picasso.with(this)
                .load(Singleton.getImageLini())
                .into(profPic);

        postButton.setEnabled(false);

        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                postButton.setEnabled(charSequence.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPostRequestThroughGraph(postText.getText().toString());
            }
        });
    }

    private void sendPostRequestThroughGraph(final String message) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content("Posting...")
                .cancelable(false)
                .build();
        dialog.show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.postForum), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setResult(100, null);
                postText.setText("");
                dialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(AddPost.this, "Unable to post. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", Singleton.getEmail());
                params.put("message", message);
                return params;
            }
        };

        Singleton.getInstance().getRequestQueue().add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("forumMessage", postText.getText().toString()).apply();
    }
}
