package np.com.aawaz.csitentrance.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.SlideUpPanelAdapter;
import np.com.aawaz.csitentrance.advance.Singleton;


public class QuizActivity extends AppCompatActivity {

    //Array list decleration
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> a = new ArrayList<>();
    ArrayList<String> b = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<String> d = new ArrayList<>();
    ArrayList<String> answer = new ArrayList<>();

    FloatingActionButton fab;
    ProgressBar pb;

    String[] colors = {" #922b72", " #2881bb", "#2b9759", "#cf1151"};


    WebView question;
    WebView option1;
    WebView option2;
    WebView option3;
    WebView option4;

    TextView feedback;
    TextView scoreTxt;
    TextView qNoTxt;

    RelativeLayout op1;
    RelativeLayout op2;
    RelativeLayout op3;
    RelativeLayout op4;

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
        pb = (ProgressBar) findViewById(R.id.progressBar);
        TextView topic = (TextView) findViewById(R.id.topic);
        ansRecy = (RecyclerView) findViewById(R.id.ansRecycle);
        slideLayout = (SlidingUpPanelLayout) findViewById(R.id.progressReport);
        question = (WebView) findViewById(R.id.question);
        option1 = (WebView) findViewById(R.id.option1);
        option2 = (WebView) findViewById(R.id.option2);
        option3 = (WebView) findViewById(R.id.option3);
        option4 = (WebView) findViewById(R.id.option4);
        feedback = (TextView) findViewById(R.id.feedback);
        scoreTxt = (TextView) findViewById(R.id.score);
        qNoTxt = (TextView) findViewById(R.id.noOfQuestion);
        op1 = (RelativeLayout) findViewById(R.id.rpl1);
        op2 = (RelativeLayout) findViewById(R.id.rpl2);
        op3 = (RelativeLayout) findViewById(R.id.rpl3);
        op4 = (RelativeLayout) findViewById(R.id.rpl4);
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
            feedback.setBackgroundColor(getResources().getColor(R.color.blueFeedback));
        } else {
            if (qNo < 9)
                feedback.setText("0" + (qNo + 1) + "");
            else if(qNo==100)
                feedback.setVisibility(View.INVISIBLE);
            else
                feedback.setText((qNo + 1) + "");
            feedback.setBackgroundColor(getResources().getColor(R.color.blueFeedback));
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

        pb.setMax(120);

        haldleProgresss();

        //Toolbar as support action bar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Load Question and Options to the ArrayList
        setDataToArrayList();

        fillTexts(qNo,false);

        onClickOption();

    }

    public void rplHandler() {
        op1.setBackgroundColor(getResources().getColor(R.color.back));
        op2.setBackgroundColor(getResources().getColor(R.color.back));
        op3.setBackgroundColor(getResources().getColor(R.color.back));
        op4.setBackgroundColor(getResources().getColor(R.color.back));
        option1.setBackgroundColor(getResources().getColor(R.color.back));
        option2.setBackgroundColor(getResources().getColor(R.color.back));
        option3.setBackgroundColor(getResources().getColor(R.color.back));
        option4.setBackgroundColor(getResources().getColor(R.color.back));
    }

    private void fillAnsRecyclerView() {
        slideUpPanelAdapter = new SlideUpPanelAdapter(this, qNo, code);
        ansRecy.setAdapter(slideUpPanelAdapter);
        ansRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
        ansRecy.scrollToPosition(slideUpPanelAdapter.getItemCount() - 1);
    }

    public boolean isLargeScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return false;
        else
            return (getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }

    private void fetchFromSp() {
        qNo = getSharedPreferences("values", MODE_PRIVATE).getInt("played" + code, 0);
        score = getSharedPreferences("values", MODE_PRIVATE).getInt("score" + code, 0);

    }

    private void fillTexts(int posi,boolean show) {
        String htm;
        if (posi == 2 && show) {
            slideLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        if (posi < 100) {
            htm = "<body bgcolor=\"" + colors[code - 1] + "\"><p style=\"color:white\">" + questions.get(posi);
            question.loadDataWithBaseURL("", htm, "text/html", "UTF-8", "");
            option1.loadDataWithBaseURL("", "a) " + a.get(posi), "text/html", "UTF-8", "");
            option2.loadDataWithBaseURL("", "b) " + b.get(posi), "text/html", "UTF-8", "");
            option3.loadDataWithBaseURL("", "c) " + c.get(posi), "text/html", "UTF-8", "");
            option4.loadDataWithBaseURL("", "d) " + d.get(posi), "text/html", "UTF-8", "");
        } else {
            question.loadDataWithBaseURL("","<body bgcolor=\"" + colors[code - 1] + "\"><p style=\"color:white\"> You have successfully completed the quiz with " + score + " score. Good Luck!!", "text/html", "UTF-8", "");
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

    private void op1Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(fab);
        }
        resetBackground();
        op1.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        option1.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        clickedAns = 1;
    }

    private void op2Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(fab);
        }
        resetBackground();
        op2.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        option2.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        clickedAns = 2;
    }

    private void op3Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(fab);
        }
        resetBackground();
        op3.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        option3.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        clickedAns = 3;
    }

    private void op4Click() {
        if (clickedAns == 0) {
            fab.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .playOn(fab);
        }
        resetBackground();
        op4.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        option4.setBackgroundColor(getResources().getColor(R.color.selectorColor));
        clickedAns = 4;
    }

    private void resetBackground() {
        op1.setBackgroundColor(getResources().getColor(R.color.back));
        op2.setBackgroundColor(getResources().getColor(R.color.back));
        op3.setBackgroundColor(getResources().getColor(R.color.back));
        op4.setBackgroundColor(getResources().getColor(R.color.back));
        option1.setBackgroundColor(getResources().getColor(R.color.back));
        option2.setBackgroundColor(getResources().getColor(R.color.back));
        option3.setBackgroundColor(getResources().getColor(R.color.back));
        option4.setBackgroundColor(getResources().getColor(R.color.back));
    }

    private void onClickOption() {
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                op1Click();
            }
        });
        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                op2Click();
            }
        });
        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                op3Click();
            }
        });
        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                op4Click();
            }
        });

        option1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                op1Click();
                return true;
            }
        });
        option2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                op2Click();
                return true;
            }
        });
        option3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                op3Click();
                return true;
            }
        });
        option4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                op4Click();
                return true;
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
                ansRecy.scrollToPosition(slideUpPanelAdapter.getItemCount() - 1);

            }


        });
    }

    public void checkAnswer() {
        if ((clickedAns == 1 && answer.get(qNo).equals("a")) || (clickedAns == 2 && answer.get(qNo).equals("b")) ||
                (clickedAns == 3 && answer.get(qNo).equals("c")) || (clickedAns == 4 && answer.get(qNo).equals("d"))) {
            feedback.setText("+1");
            feedback.setBackgroundColor(getResources().getColor(R.color.rightFeedback));
            score++;
        } else {
            feedback.setText("+0");
            feedback.setBackgroundColor(getResources().getColor(R.color.wrongFeedback));
        }
    }

    public void nextQueAndReset() {
        qNo++;
        op1.setBackgroundColor(getResources().getColor(R.color.back));
        op2.setBackgroundColor(getResources().getColor(R.color.back));
        op3.setBackgroundColor(getResources().getColor(R.color.back));
        op4.setBackgroundColor(getResources().getColor(R.color.back));
        option1.setBackgroundColor(getResources().getColor(R.color.back));
        option2.setBackgroundColor(getResources().getColor(R.color.back));
        option3.setBackgroundColor(getResources().getColor(R.color.back));
        option4.setBackgroundColor(getResources().getColor(R.color.back));
        clickedAns = 0;
        fab.setImageResource(R.drawable.ic_done_black_18dp);
        new fillTexts().execute();
    }

    private void haldleProgresss() {
        if(qNo==100)
            qNoTxt.setText("Completed");
        else
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

        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences("values", MODE_PRIVATE).edit();
        editor.putInt("played" + code, qNo);
        editor.putInt("score" + code, score);
        editor.apply();
    }


    @Override
    public void onBackPressed() {
        if (slideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            super.onBackPressed();
            finish();
        } else {
            slideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.QuizAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if(id==R.id.action_report){
            openBugReportDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openBugReportDialog() {
        final String[] cases={"The question is wrong.","Options are wrong.","Answer is wrong.","Can't say exactly what was wrong."};

        final MaterialDialog dialogMis=new MaterialDialog.Builder(this)
                .title("I found a mistake here.")
                .customView(R.layout.mistake_report, false)
                .build();
        dialogMis.show();

        final AppCompatCheckBox preCheck = (AppCompatCheckBox) dialogMis.findViewById(R.id.preCheck);

        ListView listView= (ListView) dialogMis.findViewById(R.id.options);

        LinearLayout previo= (LinearLayout) dialogMis.findViewById(R.id.previousQuestion);

        if(qNo==0)
            previo.setVisibility(View.GONE);

        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,cases));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContentValues values = new ContentValues();
                SQLiteDatabase database= Singleton.getInstance().getDatabase();
                if (preCheck.isChecked())
                    values.put("qno", qNo);
                else
                    values.put("qno", qNo + 1);
                values.put("bug", cases[i]);
                values.put("year", code+2068+"");
                database.insert("report", null, values);
                database.close();
                Toast.makeText(getApplicationContext(),"Thanks for the report.",Toast.LENGTH_SHORT).show();
                dialogMis.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class fillTexts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Thread background = new Thread() {
                public void run() {
                    try {
                        sleep(1400);
                    } catch (Exception e) {
                    }
                }
            };
            background.start();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            fillTexts(qNo,true);
            haldleProgresss();
        }
    }
}