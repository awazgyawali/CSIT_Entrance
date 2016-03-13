package np.com.aawaz.csitentrance.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SignUp extends Fragment implements TextWatcher {
    EditText name, sur, email, phone;
    SharedPreferences pref;
    FloatingActionButton fab;
    RelativeLayout profilePicture;
    CircleImageView imageView;
    private File file;

    public SignUp() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        firstTime();
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
        name = (EditText) view.findViewById(R.id.NameText);
        sur = (EditText) view.findViewById(R.id.LastText);
        phone = (EditText) view.findViewById(R.id.phoneNo);
        email = (EditText) view.findViewById(R.id.email);
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
                EasyImage.openGallery(SignUp.this, 1);
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
                    editor.putString("ImageLink", file.getPath());
                    editor.apply();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!name.getText().toString().equals("") && phone.getText().toString().length() == 10
                && !sur.getText().toString().equals("") && !email.getText().toString().equals("") && file != null) {
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
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                //Handle the image
                onPhotoReturned(imageFile);
            }
        });
    }

    private void onPhotoReturned(File imageFile) {
        this.file = imageFile;
        imageView.setImageURI(Uri.fromFile(imageFile));
    }
}
