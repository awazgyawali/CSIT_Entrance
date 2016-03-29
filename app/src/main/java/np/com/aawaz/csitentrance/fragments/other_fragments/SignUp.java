package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.misc.Singleton;

public class SignUp extends Fragment implements TextWatcher {
    EditText name, sur, email, phone;
    SharedPreferences pref;
    FloatingActionButton fab;
    RelativeLayout profilePicture;
    CircleImageView imageView;
    FancyButton fbLogin, twitterLogin;
    private CallbackManager callBackManager;
    TwitterLoginButton twitterLoginButton;
    private Bitmap bitmap;
    private View view;

    public SignUp() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        firstTime();

        handleFacebook();
        handleTwitter();
    }

    private void handleTwitter() {
        twitterLoginButton = new TwitterLoginButton(getContext());
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(Twitter.getSessionManager().getActiveSession(), new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        // Do something with the result, which provides the email address
                        email.setText(result.data);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // Do something on failure
                    }

                });

                name.setText(result.data.getUserName().split(" ")[0]);
                sur.setText(result.data.getUserName().split(" ")[1]);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });

        twitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterLoginButton.callOnClick();
            }
        });

    }

    private void handleFacebook() {
        FacebookSdk.sdkInitialize(getContext());
        callBackManager = CallbackManager.Factory.create();

        final LoginButton facebookLoginButton = new LoginButton(getContext());
        facebookLoginButton.setFragment(this);
        facebookLoginButton.setReadPermissions("email");
        facebookLoginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                postFbLoginWork();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginButton.callOnClick();
            }
        });
    }

    private void postFbLoginWork() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .content("Loading in...")
                .progress(true, 0)
                .cancelable(false)
                .build();

        dialog.show();
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,email,first_name,last_name");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", bundle, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    Snackbar.make(view, "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postFbLoginWork();
                        }
                    }).show();
                    dialog.dismiss();
                    return;
                }
                try {
                    Log.d("Debug", response.toString());
                    JSONObject object = response.getJSONObject();
                    SharedPreferences.Editor editor = pref.edit();
                    //email save garera complete login activity ma name ani email pass gareko
                    editor.putString("email", object.getString("email"));
                    editor.putString("FirstName", object.getString("first_name"));
                    editor.putString("LastName", object.getString("last_name"));
                    editor.putString("FullName", object.getString("name"));
                    editor.putString("UserID", object.getString("id"));
                    editor.apply();

                    name.setText(pref.getString("FirstName", ""));
                    sur.setText(pref.getString("LastName", ""));

                    email.setText(pref.getString("email", ""));
                    Picasso.with(getContext()).load("https://graph.facebook.com/" + pref.getString("UserID", "") + "/picture?type=large").into(imageView);
                    dialog.dismiss();
                } catch (JSONException ignored) {
                    ignored.printStackTrace();
                }
            }
        }).executeAsync();
        return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        name = (EditText) view.findViewById(R.id.NameText);
        sur = (EditText) view.findViewById(R.id.LastText);
        phone = (EditText) view.findViewById(R.id.phoneNo);
        email = (EditText) view.findViewById(R.id.email);
        fbLogin = (FancyButton) view.findViewById(R.id.fbLogin);
        twitterLogin = (FancyButton) view.findViewById(R.id.twitterLogin);
        fab = (FloatingActionButton) view.findViewById(R.id.fabBtn);
        profilePicture = (RelativeLayout) view.findViewById(R.id.profilePicChooser);
        imageView = (CircleImageView) view.findViewById(R.id.profile_image);
    }

    private void firstTime() {
        name.addTextChangedListener(this);
        sur.addTextChangedListener(this);
        phone.addTextChangedListener(this);
        email.addTextChangedListener(this);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().toString().length() != 10 || !email.getText().toString().contains(".com") || !email.getText().toString().contains("@")) {
                    if (phone.getText().toString().length() != 10) {
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(phone);
                        Toast.makeText(getContext(), "Invalid mobile number.", Toast.LENGTH_SHORT).show();
                        phone.setText("");
                    } else {
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(email);
                        Toast.makeText(getContext(), "Invalid email.", Toast.LENGTH_SHORT).show();
                        email.setText("");
                    }
                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Name", name.getText().toString());
                    editor.putString("Surname", sur.getText().toString());
                    editor.putString("E-mail", email.getText().toString());
                    editor.putString("PhoneNo", phone.getText().toString() + "");
                    editor.putString("uniqueID", email.getText().toString());
                    editor.apply();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });
    }

    public void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!name.getText().toString().equals("") && phone.getText().toString().length() == 10
                && !sur.getText().toString().equals("") && !email.getText().toString().equals("") && imageView.getDrawable() != null) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();


            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

            imageView.setImageBitmap(yourSelectedImage);
            decodeFile(filePath);
        }
    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 256;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        imageView.setImageBitmap(bitmap);
    }


    private void onPhotoReturned(File imageFile) {


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();
        final String file = Base64.encodeToString(data, Base64.DEFAULT);

        StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.uploadImage), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("debug", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("base64", file);
                params.put("ImageName", Singleton.getEmail());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Singleton.getInstance().getRequestQueue().add(request);
    }
}
