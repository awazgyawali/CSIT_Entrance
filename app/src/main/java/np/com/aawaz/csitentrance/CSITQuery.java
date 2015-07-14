package np.com.aawaz.csitentrance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class CSITQuery extends AppCompatActivity {

    Button send;
    EditText text;
    RecyclerView query;
    QueryAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csitquery);
        setSupportActionBar((Toolbar) findViewById(R.id.queryToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        ArrayList<String> messages=new ArrayList<>();


        ArrayList<Integer> flag = new ArrayList<>();

        send = (Button) findViewById(R.id.sendBtn);
        text = (EditText) findViewById(R.id.text);
        query = (RecyclerView) findViewById(R.id.recyQuery);

        adapter = new QueryAdapter(this, messages, flag);
        query.setLayoutManager(new LinearLayoutManager(this));
        query.setAdapter(adapter);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendSms(View v) {
        adapter.added(text.getText().toString());
        text.setText("");

    }


}
