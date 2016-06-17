package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class ReportCardActivity extends AppCompatActivity {
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_card);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        new EventSender().logEvent("report_card");

        String card_title = getIntent().getStringExtra("title");
        final String subject_code = getIntent().getStringExtra("subject");
        final String yearCode = getIntent().getStringExtra("code");
        final int play = getIntent().getIntExtra("played", 100);
        int scored = getIntent().getIntExtra("scored", 0);

        String[] codes = {SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072,
                SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4};

        for (int i = 0; i < codes.length; i++)
            if (codes[i].equals(yearCode))
                position = i;

        CircleImageView imageView = (CircleImageView) findViewById(R.id.image_report);
        RobotoTextView name = (RobotoTextView) findViewById(R.id.name_report),
                total_score = (RobotoTextView) findViewById(R.id.total_report),
                title = (RobotoTextView) findViewById(R.id.title_report),
                played = (RobotoTextView) findViewById(R.id.played_report),
                score = (RobotoTextView) findViewById(R.id.score_report),
                accuracy = (RobotoTextView) findViewById(R.id.accuracy_report),
                played_total = (RobotoTextView) findViewById(R.id.played_report_total),
                score_total = (RobotoTextView) findViewById(R.id.score_report_total),
                accuracy_total = (RobotoTextView) findViewById(R.id.accuracy_report_total);
        TextView fab = (TextView) findViewById(R.id.reply_report);

        title.setText(card_title);
        played.setText(String.valueOf(play));
        score.setText(String.valueOf(scored));
        total_score.setText("Total Score: " + SPHandler.getInstance().getTotalScore());
        name.setText(user.getDisplayName());
        accuracy.setText(getAccuracy(play, scored));

        played_total.setText(String.valueOf(SPHandler.getInstance().getTotalPlayed()));
        score_total.setText(String.valueOf(SPHandler.getInstance().getTotalScore()));
        accuracy_total.setText(getAccuracy(SPHandler.getInstance().getTotalPlayed(), SPHandler.getInstance().getTotalScore()));

        Picasso.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);

        //handling reply
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play == 25) {
                    Intent intent = new Intent(ReportCardActivity.this, YearQuizActivity.class);
                    intent.putExtra("code", yearCode);
                    intent.putExtra("subject", subject_code);
                    intent.putExtra("position", position);

                    startActivity(intent);
                    finish();
                } else {
                    SPHandler.getInstance().setScore(yearCode, 0);
                    SPHandler.getInstance().setPlayed(yearCode, 0);

                    Intent intent = new Intent(ReportCardActivity.this, YearQuizActivity.class);

                    intent.putExtra("code", yearCode);
                    intent.putExtra("position", position);

                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private String getAccuracy(int play, int scored) {
        int acc = (int) (((float) scored / (float) play) * 100);
        return acc + "%";
    }
}
