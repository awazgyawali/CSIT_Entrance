package np.com.aawaz.csitentrance.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.devspark.robototextview.widget.RobotoTextView;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.FullQuestionAdapter;


public class FullQuestionActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_question_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolabrFullQuestion));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        int code = getIntent().getIntExtra("code", 0);

        recyclerView = (RecyclerView) findViewById(R.id.fullQuestionRecyclerView);
        recyclerView.setAdapter(new FullQuestionAdapter(this, code));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RobotoTextView title = (RobotoTextView) findViewById(R.id.titleFullQuestion);

        title.setText(getResources().getStringArray(R.array.years)[code - 1]);
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
