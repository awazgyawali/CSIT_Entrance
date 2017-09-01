package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.AnswerDialog;
import np.com.aawaz.csitentrance.custom_views.CustomViewPager;
import np.com.aawaz.csitentrance.fragments.other_fragments.AnswersDrawer;
import np.com.aawaz.csitentrance.fragments.other_fragments.PopupDialogFragment;
import np.com.aawaz.csitentrance.fragments.other_fragments.QuestionFragment;
import np.com.aawaz.csitentrance.interfaces.OnDismissListener;
import np.com.aawaz.csitentrance.interfaces.QuizInterface;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class SubjectQuizActivity extends AppCompatActivity implements QuizInterface {

    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> a = new ArrayList<>();
    ArrayList<String> b = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<String> d = new ArrayList<>();
    ArrayList<String> answer = new ArrayList<>();

    DrawerLayout drawerLayout;
    AnswersDrawer answersDrawer;
    CustomViewPager customViewPager;

    int qNo;
    String code, subject;
    int index;
    private TextView scoreText;
    private SPHandler spHandler;
    private String mUrl;
    private String mTitle;
    private int score = 0;

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        //Toolbar as support action bar
        setTitle("");
        setSupportActionBar((Toolbar) findViewById(R.id.quizToolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        code = getIntent().getStringExtra("code");
        subject = getIntent().getStringExtra("subject");

        spHandler = SPHandler.getInstance();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutQuiz);
        customViewPager = (CustomViewPager) findViewById(R.id.viewPagerQuestion);
        answersDrawer = (AnswersDrawer) getSupportFragmentManager().findFragmentById(R.id.answerFragment);

        //Load and Options to the ArrayList
        setDataToArrayList();

        answersDrawer.setInitialForSubject(qNo, getIntent().getIntExtra("position", 1) + 1, index);

        initializeViewPager();

        setHeader();

        appIndexing();

    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/questions";
        mTitle = "BSc CSIT Entrance Qld Questions";
    }

    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    private void setHeader() {
        CircleImageView quizProf = (CircleImageView) findViewById(R.id.quizProfilePic);
        TextView name = (TextView) findViewById(R.id.quizName);
        scoreText = (TextView) findViewById(R.id.quizScore);
        Picasso.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(quizProf);

        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        scoreText.setText("Your Score: " + score);
    }

    private void initializeViewPager() {
        customViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return QuestionFragment.newInstance(getIntent().getIntExtra("position", 1),
                        position,
                        questions.get(position),
                        a.get(position),
                        b.get(position),
                        c.get(position),
                        d.get(position),
                        answer.get(position),
                        subject)
                        .setListener(SubjectQuizActivity.this);
            }

            @Override
            public int getCount() {
                return questions.size();
            }
        });
        customViewPager.setCurrentItem(qNo);
    }

    private void showDialogAd() {
        PopupDialogFragment popupDialogFragment = new PopupDialogFragment();
        popupDialogFragment.show(getSupportFragmentManager(), "popup");
    }

    @Override
    public void selected(CardView submit, boolean correct, String answer) {
        spHandler.increasePlayed(subject);
        spHandler.increaseTimesPlayed();
        if ((spHandler.getTimesPlayed() % 10) == 0) {
            showDialogAd();
        }
        if (customViewPager.getCurrentItem() != 24)
            answersDrawer.increaseSize();
        else {
            startActivity(new Intent(SubjectQuizActivity.this, ReportCardActivity.class)
                    .putExtra("title", SPHandler.getInstance().getSubjectName(subject))
                    .putExtra("subject", subject)
                    .putExtra("code", code)
                    .putExtra("played", 25)
                    .putExtra("scored", score)
                    .putExtra("index", index));
            finish();
        }
        if (correct) {
            score++;
            spHandler.increaseScore(subject);
            scoreText.setText("Your Score: " + score);
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorSelected));
            TextView text = (TextView) submit.findViewById(R.id.submit_button_text);
            text.setText("Correct");
            submit.setOnClickListener(null);
            YoYo.with(Techniques.Tada).duration(500).playOn(submit);
        } else {
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.background_1));
            TextView text = (TextView) submit.findViewById(R.id.submit_button_text);
            text.setText("In-Correct");
            submit.setOnClickListener(null);
            YoYo.with(Techniques.Shake).duration(500).playOn(submit);
        }

        if (spHandler.shouldShowAnswers() && !correct) {
            AnswerDialog answerDialog = AnswerDialog.newInstance(answer);
            answerDialog.show(getSupportFragmentManager(), "answer");
            answerDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss() {
                    changeQuestion(200);
                }
            });
        } else
            changeQuestion(500);
    }

    private void changeQuestion(long time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (customViewPager.getCurrentItem() != 99)
                    customViewPager.setCurrentItem(customViewPager.getCurrentItem() + 1);
            }
        }, time);
    }

    public void setDataToArrayList() {
        index = SPHandler.getInstance().getIndexOfQuestion(getIntent().getIntExtra("position", -1), subject);
        int maxSize = index + 25;
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + (getIntent().getIntExtra("position", 1) + 1) + ".json", this));
            JSONArray m_jArry = obj.getJSONArray("questions");
            for (int i = index; i < maxSize; i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                questions.add(jo_inside.getString("question"));
                a.add(jo_inside.getString("a"));
                b.add(jo_inside.getString("b"));
                c.add(jo_inside.getString("c"));
                d.add(jo_inside.getString("d"));
                answer.add(jo_inside.getString("ans"));
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_answer) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
