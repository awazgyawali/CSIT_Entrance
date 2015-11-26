package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.MainRecyclerAdapter;
import np.com.aawaz.csitentrance.advance.MyApplication;

public class PlayQuiz extends AppCompatActivity implements MainRecyclerAdapter.ClickListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_questions);
        setSupportActionBar((Toolbar) findViewById(R.id.modelQueToolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String[] titles = {"Model Question 1", "Model Question 2", "Model Question 3"
                , "Model Question 4", "Model Question 5"};
        int primaryColors[] = {R.color.primary2, R.color.primary3, R.color.primary4,
                R.color.primary5, R.color.primary6};
        int darkColors[] = {R.color.dark2, R.color.dark3, R.color.dark4,
                R.color.dark5, R.color.dark6};
        int icon[] = {R.drawable.play_small, R.drawable.play_small, R.drawable.play_small, R.drawable.play_small,
                R.drawable.play_small};
        int images[] = {R.drawable.book, R.drawable.book, R.drawable.book, R.drawable.book,
                R.drawable.book};


        MainRecyclerAdapter adater = new MainRecyclerAdapter(this, primaryColors, darkColors, icon, titles, images);

        RecyclerView listRecy = (RecyclerView) findViewById(R.id.modelQuestionRecy);
        listRecy.setAdapter(adater);
        listRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
        adater.setClickListner(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("position", position + 4);
        startActivity(intent);
    }

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
