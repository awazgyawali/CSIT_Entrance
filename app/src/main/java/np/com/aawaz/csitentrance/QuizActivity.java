package np.com.aawaz.csitentrance;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.LayoutRipple;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class QuizActivity extends AppCompatActivity {

    //Array list decleration
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> a = new ArrayList<>();
    ArrayList<String> b = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<String> d = new ArrayList<>();
    ArrayList<String> answer = new ArrayList<>();

    FloatingActionButton fab;
    ProgressBarDeterminate pb;

    String[] colors = {" #1de9b6", " #ffeb3c", "#03a9f5", "#8bc34a"};


    WebView question;
    WebView option1;
    WebView option2;
    WebView option3;
    WebView option4;
    TextView feedback;
    TextView scoreTxt;
    TextView qNoTxt;

    LayoutRipple op1;
    LayoutRipple op2;
    LayoutRipple op3;
    LayoutRipple op4;

    SlideUpPanelAdapter slideUpPanelAdapter;
    RecyclerView ansRecy;

    SlidingUpPanelLayout slideLayout;

    String[] titles = {"Score Board", "2069 Entrance Question", "2070 Entrance Question", "2071 Entrance Question", "Model Question"};

    int qNo = 0;
    int code;
    int score = 0;
    int clickedAns = 0;

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

        loadAd();

        YoYo.with(Techniques.FadeOut)
                .duration(0)
                .playOn(findViewById(R.id.quizMainLayout));
        YoYo.with(Techniques.SlideInRight)
                .duration(1000)
                .delay(1500)
                .playOn(findViewById(R.id.quizMainLayout));

        //Initializing colors array
        int primaryColors[] = {R.color.primary1, R.color.primary2, R.color.primary3, R.color.primary4, R.color.primary5,
                R.color.primary6, R.color.primary7, R.color.primary8, R.color.primary9, R.color.primary10};
        int darkColors[] = {R.color.dark1, R.color.dark2, R.color.dark3, R.color.dark4, R.color.dark5,
                R.color.dark6, R.color.dark7, R.color.dark8, R.color.dark9, R.color.dark10};

        //Initlilixing view's
        Toolbar toolbar = (Toolbar) findViewById(R.id.quizToolbar);
        RelativeLayout questionLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        fab = (FloatingActionButton) findViewById(R.id.AnswerFab);
        fab.bringToFront();


        int avatar[] = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five,
                R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten,
                R.drawable.eleven, R.drawable.twelve};

        ImageView img = (ImageView) findViewById(R.id.profQue);
        img.setImageDrawable(getResources().getDrawable(avatar[(getSharedPreferences("info", MODE_PRIVATE).getInt("Avatar", 1)) - 1]));
        pb = (ProgressBarDeterminate) findViewById(R.id.progressBar);
        TextView topic = (TextView) findViewById(R.id.topic);
        slideLayout = (SlidingUpPanelLayout) findViewById(R.id.progressReport);
        question = (WebView) findViewById(R.id.question);
        option1 = (WebView) findViewById(R.id.option1);
        option2 = (WebView) findViewById(R.id.option2);
        option3 = (WebView) findViewById(R.id.option3);
        option4 = (WebView) findViewById(R.id.option4);
        feedback = (TextView) findViewById(R.id.feedback);
        scoreTxt = (TextView) findViewById(R.id.score);
        qNoTxt = (TextView) findViewById(R.id.noOfQuestion);
        op1 = (LayoutRipple) findViewById(R.id.rpl1);
        op2 = (LayoutRipple) findViewById(R.id.rpl2);
        op3 = (LayoutRipple) findViewById(R.id.rpl3);
        op4 = (LayoutRipple) findViewById(R.id.rpl4);
        op1.bringToFront();
        op2.bringToFront();
        op3.bringToFront();
        op4.bringToFront();
        feedback.bringToFront();

        rplHandler();

        //Receiving intent
        code = getIntent().getExtras().getInt("position");
        topic.setText(titles[code]);

        RelativeLayout coreLayout = (RelativeLayout) findViewById(R.id.coreLayout);
        coreLayout.setBackgroundColor(getResources().getColor(darkColors[code]));


        //Last Stage of the game
        fetchFromSp();

        fillAnsRecyclerView();

        //Initial Notification

        if (qNo == 0) {
            feedback.setText("GO!");
            feedback.setBackgroundColor(getResources().getColor(R.color.primary4));
        } else {
            if (qNo < 9)
                feedback.setText("0" + (qNo + 1) + "");
            else
                feedback.setText((qNo + 1) + "");

            feedback.setBackgroundColor(getResources().getColor(R.color.primary4));
        }
        YoYo.with(Techniques.SlideInDown)
                .duration(700)
                .playOn(feedback);
        YoYo.with(Techniques.SlideOutDown)
                .duration(700)
                .delay(800)
                .playOn(feedback);

        //Setting color
        toolbar.setBackgroundColor(getResources().getColor(primaryColors[code]));
        questionLayout.setBackgroundColor(getResources().getColor(primaryColors[code]));
        pb.setBackgroundColor(getResources().getColor(primaryColors[code]));
        pb.setMax(120);
        pb.setMin(0);
        haldleProgresss();


        //Toolbar as support action bar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Load Question and Options to the ArrayList
        setDataToArrayList();


        fillTexts(qNo);
        onClickOption();
    }

    public void rplHandler() {

        op1.setBackgroundColor(getResources().getColor(R.color.back));
        op2.setBackgroundColor(getResources().getColor(R.color.back));
        op3.setBackgroundColor(getResources().getColor(R.color.back));
        op4.setBackgroundColor(getResources().getColor(R.color.back));
        op1.setRippleColor(getResources().getColor(R.color.toolbar_color));
        op2.setRippleColor(getResources().getColor(R.color.toolbar_color));
        op3.setRippleColor(getResources().getColor(R.color.toolbar_color));
        op4.setRippleColor(getResources().getColor(R.color.toolbar_color));
    }

    private void fillAnsRecyclerView() {
        ansRecy = (RecyclerView) findViewById(R.id.ansRecycle);
        slideUpPanelAdapter = new SlideUpPanelAdapter(this, qNo, code);
        ansRecy.setAdapter(slideUpPanelAdapter);
        ansRecy.setLayoutManager(new LinearLayoutManager(this));
        ansRecy.scrollToPosition(slideUpPanelAdapter.size - 1);
    }

    private void fetchFromSp() {
        qNo = getSharedPreferences("values", MODE_PRIVATE).getInt("played" + code, 0);
        score = getSharedPreferences("values", MODE_PRIVATE).getInt("score" + code, 0);

    }

    private void fillTexts(int posi) {
        String htm;
        if (posi < 100) {
            if (code == 4) {
                htm = "<body bgcolor=\"" + colors[code - 1] + "\">" + questions.get(posi);
            } else {
                htm = "<body bgcolor=\"" + colors[code - 1] + "\">" + questions.get(posi);
            }
            question.loadDataWithBaseURL("", htm, "text/html", "UTF-8", "");
            option1.loadDataWithBaseURL("", "a) " + a.get(posi), "text/html", "UTF-8", "");
            option2.loadDataWithBaseURL("", "b) " + b.get(posi), "text/html", "UTF-8", "");
            option3.loadDataWithBaseURL("", "c) " + c.get(posi), "text/html", "UTF-8", "");
            option4.loadDataWithBaseURL("", "d) " + d.get(posi), "text/html", "UTF-8", "");

        } else {
            question.loadDataWithBaseURL("", "You have successfully completed your game with " + score + " score. Good Luck!!", "text/html", "UTF-8", "");
            gameCompletedHandler();
        }
    }

    private void gameCompletedHandler() {
        op1.setVisibility(View.INVISIBLE);
        op2.setVisibility(View.INVISIBLE);
        op3.setVisibility(View.INVISIBLE);
        op4.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);

    }

    private void onClickOption() {
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedAns == 0) {
                    fab.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceIn)
                            .duration(700)
                            .playOn(fab);
                }
                op1.setBackgroundColor(getResources().getColor(R.color.back));
                op2.setBackgroundColor(getResources().getColor(R.color.back));
                op3.setBackgroundColor(getResources().getColor(R.color.back));
                op4.setBackgroundColor(getResources().getColor(R.color.back));
                op1.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
                clickedAns = 1;
            }
        });
        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedAns == 0) {
                    fab.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceIn)
                            .duration(700)
                            .playOn(fab);
                }
                op1.setBackgroundColor(getResources().getColor(R.color.back));
                op2.setBackgroundColor(getResources().getColor(R.color.back));
                op3.setBackgroundColor(getResources().getColor(R.color.back));
                op4.setBackgroundColor(getResources().getColor(R.color.back));
                op2.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
                clickedAns = 2;
            }
        });
        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedAns == 0) {
                    fab.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceIn)
                            .duration(700)
                            .playOn(fab);
                }
                op1.setBackgroundColor(getResources().getColor(R.color.back));
                op2.setBackgroundColor(getResources().getColor(R.color.back));
                op3.setBackgroundColor(getResources().getColor(R.color.back));
                op4.setBackgroundColor(getResources().getColor(R.color.back));
                op3.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
                clickedAns = 3;
            }
        });
        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedAns == 0) {
                    fab.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceIn)
                            .duration(700)
                            .playOn(fab);
                }
                op1.setBackgroundColor(getResources().getColor(R.color.back));
                op2.setBackgroundColor(getResources().getColor(R.color.back));
                op3.setBackgroundColor(getResources().getColor(R.color.back));
                op4.setBackgroundColor(getResources().getColor(R.color.back));
                op4.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
                clickedAns = 4;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
                fab.setVisibility(View.INVISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .duration(700)
                        .playOn(feedback);
                YoYo.with(Techniques.SlideOutLeft)
                        .duration(700)
                        .playOn(findViewById(R.id.quizMainLayout));
                YoYo.with(Techniques.SlideOutDown)
                        .duration(700)
                        .delay(600)
                        .playOn(feedback);
                YoYo.with(Techniques.SlideInRight)
                        .duration(700)
                        .delay(1000)
                        .playOn(findViewById(R.id.quizMainLayout));
                nextQueAndReset();
                slideUpPanelAdapter.increaseSize();
                ansRecy.scrollToPosition(slideUpPanelAdapter.size - 1);

            }


        });
    }

    public void checkAnswer() {
        if ((clickedAns == 1 && answer.get(qNo).equals("a")) || (clickedAns == 2 && answer.get(qNo).equals("b")) ||
                (clickedAns == 3 && answer.get(qNo).equals("c")) || (clickedAns == 4 && answer.get(qNo).equals("d"))) {
            feedback.setText("+1");
            feedback.setBackgroundColor(getResources().getColor(R.color.primary5));
            score++;
        } else {
            feedback.setText("+0");
            feedback.setBackgroundColor(getResources().getColor(R.color.primary6));
        }
    }

    public void nextQueAndReset() {
        qNo++;
        op1.setBackgroundColor(getResources().getColor(R.color.back));
        op2.setBackgroundColor(getResources().getColor(R.color.back));
        op3.setBackgroundColor(getResources().getColor(R.color.back));
        op4.setBackgroundColor(getResources().getColor(R.color.back));
        clickedAns = 0;
        fab.setColorNormal(getResources().getColor(R.color.dark1));
        fab.setImageResource(R.drawable.ic_done_black_18dp);
        fillTexts(qNo);
        haldleProgresss();
    }

    private void haldleProgresss() {
        qNoTxt.setText((qNo + 1) + " / 100");
        scoreTxt.setText(score + "");
        pb.setProgress(21 + qNo);

    }

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json", this));
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

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences("values", MODE_PRIVATE).edit();
        editor.putInt("played" + code, qNo);
        editor.putInt("score" + code, score);
        editor.commit();
        finish();
    }

    public void loadAd() {
        AdView mAdView = (AdView) findViewById(R.id.QuizAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (slideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            finish();
        }
    }
}