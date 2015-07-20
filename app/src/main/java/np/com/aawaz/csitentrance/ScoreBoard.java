package np.com.aawaz.csitentrance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;


public class ScoreBoard extends AppCompatActivity {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> scores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ScoreToolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        YoYo.with(Techniques.SlideInDown)
                .duration(1500)
                .playOn(findViewById(R.id.coreLayoutScore));


        MaterialDialog dialogInitial = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .progress(true, 0)
                .build();
        dialogInitial.show();

    }

    private void callFillRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.scoreBoardRecyclerView);
        mRecyclerView.setAdapter(new ScoreBoardAdapter(this, names, scores));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
