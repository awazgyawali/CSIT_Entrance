package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.fragments.other_fragments.AnswersDrawer;
import np.com.aawaz.csitentrance.fragments.other_fragments.QuestionFragment;
import np.com.aawaz.csitentrance.interfaces.QuizInterface;
import np.com.aawaz.csitentrance.custom_views.CustomViewPager;
import np.com.aawaz.csitentrance.misc.SPHandler;


public class QuizActivity extends AppCompatActivity implements QuizInterface {

    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> a = new ArrayList<>();
    ArrayList<String> b = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<String> d = new ArrayList<>();
    ArrayList<String> answer = new ArrayList<>();

    DrawerLayout drawerLayout;
    AnswersDrawer answersDrawer;
    CustomViewPager customViewPager;
    String[] subjectArray = {SPHandler.PHYSICS, SPHandler.CHEMISTRY, SPHandler.MATH, SPHandler.ENGLISH};

    int qNo;
    int score;
    String code;
    private RobotoTextView scoreText;
    private SPHandler spHandler;

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
        spHandler = SPHandler.getInstance();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutQuiz);
        customViewPager = (CustomViewPager) findViewById(R.id.viewPagerQuestion);
        answersDrawer = (AnswersDrawer) getSupportFragmentManager().findFragmentById(R.id.answerFragment);

        //Last Stage of the game
        fetchFromSp();

        //Load and Options to the ArrayList
        setDataToArrayList();

        initilizeViewPager();

        setHeader();
    }

    private void setHeader() {
        CircleImageView quizProf = (CircleImageView) findViewById(R.id.quizProfilePic);
        RobotoTextView name = (RobotoTextView) findViewById(R.id.quizName);
        scoreText = (RobotoTextView) findViewById(R.id.quizScore);
        Picasso.with(this)
                .load(SPHandler.getInstance().getImageLink())
                .into(quizProf);

        name.setText(SPHandler.getInstance().getFullName());

        scoreText.setText("Your Score: " + spHandler.getScore(code));
    }

    private void initilizeViewPager() {
        customViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return QuestionFragment.newInstance(position, questions.get(position), a.get(position),
                        b.get(position), c.get(position), d.get(position), answer.get(position))
                        .setListener(QuizActivity.this);
            }

            @Override
            public int getCount() {
                return 100;
            }
        });
        customViewPager.setCurrentItem(qNo);
    }

    private void fetchFromSp() {
        qNo = spHandler.getPlayed(code);
        score = spHandler.getScore(code);

        answersDrawer.setInitialData(qNo, getIntent().getIntExtra("position", 1));
    }

    @Override
    public void selected(CardView submit, boolean correct) {
        spHandler.increasePlayed(code);
        spHandler.increasePlayed(subjectArray[customViewPager.getCurrentItem() / 25]);
        if (correct) {
            spHandler.increaseScore(code);
            spHandler.increaseScore(subjectArray[customViewPager.getCurrentItem() / 25]);
            answersDrawer.increaseSize();
            scoreText.setText("Your Score: " + spHandler.getScore(code));
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorSelected));
            RobotoTextView text = (RobotoTextView) submit.findViewById(R.id.submit_button_text);
            text.setText("Correct");
            submit.setOnClickListener(null);
            YoYo.with(Techniques.Tada).duration(500).playOn(submit);
        } else {
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.background_1));
            RobotoTextView text = (RobotoTextView) submit.findViewById(R.id.submit_button_text);
            text.setText("In-Correct");
            submit.setOnClickListener(null);
            YoYo.with(Techniques.Shake).duration(500).playOn(submit);
        }
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                customViewPager.setCurrentItem(customViewPager.getCurrentItem() + 1);
            }
        });
    }

    public void setDataToArrayList() {
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + getIntent().getIntExtra("position", 1) + ".json", this));
            JSONArray m_jArry = obj.getJSONArray("questions");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                questions.add(jo_inside.getString("question"));
                a.add(jo_inside.getString("a"));
                b.add(jo_inside.getString("b"));
                c.add(jo_inside.getString("c"));
                d.add(jo_inside.getString("d"));
                answer.add(jo_inside.getString("ans"));
            }
        } catch (Exception ignored) {
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