package np.com.aawaz.csitentrance.advance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import np.com.aawaz.csitentrance.R;

public class CrashReportSender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report_sender);
        new MaterialDialog.Builder(this)
                .title("CSIT Entrance crashed")
                .content("Ooops! I crashed but please report to my developer so that he could fix me out.")
                .positiveText("Send Report")
                .negativeText("Cancel")
                .positiveColor(getResources().getColor(R.color.green))
                .negativeColor(getResources().getColor(R.color.red))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        startActivity(new Intent(CrashReportSender.this, CrashReportSender.class));
                    }
                })
                .build()
                .show();
    }
}
