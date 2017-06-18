package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout name, email, password;
    TextInputEditText nameText, emailText, passwordText;
    TextView buttonSignup;
    CircleImageView imageView;
    ProgressBar loading;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setSupportActionBar((Toolbar) findViewById(R.id.signUpToolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create an account");

        firebaseAuth = FirebaseAuth.getInstance();

        nameText = (TextInputEditText) findViewById(R.id.signUpName);
        emailText = (TextInputEditText) findViewById(R.id.signUpEmail);
        passwordText = (TextInputEditText) findViewById(R.id.signUpPassword);

        name = (TextInputLayout) findViewById(R.id.signUpNameLayout);
        email = (TextInputLayout) findViewById(R.id.signUpEmailLayout);
        password = (TextInputLayout) findViewById(R.id.signUpPasswordLayout);

        imageView = (CircleImageView) findViewById(R.id.profilePictureChooser);

        buttonSignup = (TextView) findViewById(R.id.buttonSignUp);

        loading = (ProgressBar) findViewById(R.id.loadingSignUp);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery =
                        new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 1001);
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEverything()) {
                    buttonSignup.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                            .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    uploadImage();
                                }
                            })
                            .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (e instanceof FirebaseAuthUserCollisionException)
                                        email.setError("Email is already associated with an account. Try logging in.");
                                    buttonSignup.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
    }

    private boolean validateEverything() {
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        boolean flag = true;
        if (nameText.getText().length() <= 0) {
            name.setError("Name must not be empty.");
            flag = false;
        }

        if (!emailText.getText().toString().matches(emailPattern)) {
            email.setError("Please enter valid e-mail address.");
            flag = false;
        }

        if (passwordText.getText().length() == 0) {
            password.setError("Password cannot be empty.");
            flag = false;
        } else if (passwordText.getText().length() < 6) {
            password.setError("Password must be longer than 6 characters..");
            flag = false;
        }
        return flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                }
        }
    }

    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://csit-entrance-7d58.appspot.com");

        StorageReference imagesRef = storageRef.child("profile_pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                buttonSignup.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nameText.getText().toString())
                        .setPhotoUri(downloadUrl)
                        .build();

                firebaseAuth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    new EventSender().logEvent("email_signedup");
                                    setResult(200);
                                    finish();
                                } else {
                                    buttonSignup.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.GONE);
                                    task.getException().printStackTrace();
                                    Toast.makeText(SignUpActivity.this, "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
