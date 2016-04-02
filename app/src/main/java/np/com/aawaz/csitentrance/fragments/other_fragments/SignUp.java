package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.misc.SPHandler;

public class SignUp extends Fragment implements TextWatcher {
    EditText name, sur, email, phone;
    FloatingActionButton fab;
    RelativeLayout profilePicture;
    CircleImageView imageView;
    FancyButton fbLogin;
    private CallbackManager callBackManager;
    private View view;

    public SignUp() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SPHandler.getInstance().setImageLink(null);
        firstTime();

        handleFacebook();

        handleButtons();
    }

    private void handleButtons() {
        if (SPHandler.getInstance().isSocialLoggedIn()) {
            fbLogin.setVisibility(View.GONE);
        }
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
                SPHandler.getInstance().setSocialLoggedIn();
                handleButtons();
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
                    final JSONObject object = response.getJSONObject();
                    //email save garera complete login activity ma name ani email pass gareko
                    email.setText(object.getString("email"));
                    name.setText(object.getString("first_name"));
                    sur.setText(object.getString("last_name"));
                    Picasso.with(getContext()).load("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large").into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            try {
                                SPHandler.getInstance().setImageLink("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                                imageView.setImageBitmap(bitmap);
                                onTextChanged("", 1, 1, 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
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
                SPHandler.getInstance().saveLoginData(name.getText().toString(), sur.getText().toString(), email.getText().toString(), phone.getText().toString());
                SPHandler.getInstance().setLoggedIn();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!name.getText().toString().equals("") && phone.getText().toString().length() == 10
                && !sur.getText().toString().equals("") && !email.getText().toString().equals("")
                && SPHandler.getInstance().getImageLink() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        callBackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK
                && data != null) {
            try {
                final Uri imageUri = data.getData();
                SPHandler.getInstance().setImageLink(imageUri.toString());
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                onTextChanged("", 1, 1, 1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
