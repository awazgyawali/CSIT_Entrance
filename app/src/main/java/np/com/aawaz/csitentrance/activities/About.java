package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;

public class About extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.activity_about);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarAbout));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Button feedback = (Button) findViewById(R.id.fed);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarAbout);
        final CircleImageView logo = (CircleImageView) findViewById(R.id.logoView);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedBack();
            }
        });

        likeButton();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbarAbout);
        collapsingToolbarLayout.setTitle("About Us");
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    public void feedBack() {
        Intent sendMail = new Intent(Intent.ACTION_SEND);
        sendMail.setData(Uri.parse("mailto:"));
        String[] to = {"csitentrance@gmail.com"};
        sendMail.putExtra(Intent.EXTRA_EMAIL, to);
        sendMail.putExtra(Intent.EXTRA_SUBJECT, "Regarding CSIT Entrance Android Application.");
        sendMail.setType("message/rfc822");
        Intent chooser = Intent.createChooser(sendMail, "Send e-mail");
        startActivity(chooser);
    }

    private void likeButton() {
        LikeView likeView = (LikeView) findViewById(R.id.likeButton);
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        likeView.setObjectIdAndType(
                "https://www.facebook.com/CSITentrance", LikeView.ObjectType.PAGE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
