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


public class FullQuestion extends AppCompatActivity implements MainRecyclerAdapter.ClickListner {

    RecyclerView listRecy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_question);
        setSupportActionBar((Toolbar) findViewById(R.id.fullQueToolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String[] titles = {"2069 Question", "2070 Question", "2071 Question", "Model Question 1", "Model Question 2", "Model Question 3"
                , "Model Question 4", "Model Question 5"};
        int primaryColors[] = {R.color.primary1, R.color.primary2, R.color.primary3, R.color.primary4,
                R.color.primary5, R.color.primary6, R.color.primary7, R.color.primary8};
        int darkColors[] = {R.color.dark1, R.color.dark2, R.color.dark3, R.color.dark4,
                R.color.dark5, R.color.dark6, R.color.dark7, R.color.dark8};
        int icon[] = {R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp,
                R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp};
        int images[] = {R.drawable.full_question, R.drawable.full_question, R.drawable.full_question, R.drawable.full_question,
                R.drawable.full_question, R.drawable.full_question, R.drawable.full_question, R.drawable.full_question};


        MainRecyclerAdapter adater = new MainRecyclerAdapter(this, primaryColors, darkColors, icon, titles, images);

        listRecy = (RecyclerView) findViewById(R.id.onlineRecyc);
        listRecy.setAdapter(adater);
        listRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
        adater.setClickListner(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("code", position + 1);
        startActivity(intent);
    }

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
