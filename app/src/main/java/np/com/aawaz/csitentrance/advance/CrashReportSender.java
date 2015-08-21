package np.com.aawaz.csitentrance.advance;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import np.com.aawaz.csitentrance.R;

public class CrashReportSender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MaterialDialog.Builder(this)
                .title("CSIT Entrance crashed")
                .content("Ooops! I crashed but please report to my developer so that he could fix me out.")
                .positiveText("Send Report")
                .negativeText("Cancel")
                .positiveColor(getResources().getColor(R.color.green))
                .negativeColor(getResources().getColor(R.color.red))
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        sendReport();
                        Toast.makeText(CrashReportSender.this, "Thanks for your report. We will optimize the issue soon.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        finish();
                    }
                })
                .build()
                .show();
    }

    private void sendReport() {
        String detail = getIntent().getExtras().getString("CrashDetail");
    }
}
