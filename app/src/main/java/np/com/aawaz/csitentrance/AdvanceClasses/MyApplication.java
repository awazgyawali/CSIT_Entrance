package np.com.aawaz.csitentrance.AdvanceClasses;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSenderException;

import np.com.aawaz.csitentrance.R;

@ReportsCrashes(mailTo = "contact@aawaz.com.np",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text // optional. displays a Toast message when the user accepts to send a report.
)
public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }


    @Override
    public void onCreate() {
        sInstance = this;
        ACRA.init(this);
        ReportSender yourSender = new ReportSender();
        ACRA.getErrorReporter().setReportSender(yourSender);
        super.onCreate();
    }

class ReportSender implements org.acra.sender.ReportSender{

    @Override
    public void send(Context context, CrashReportData crashReportData) throws ReportSenderException {
        Intent sendMail = new Intent(Intent.ACTION_SEND);
        sendMail.setData(Uri.parse("mailto:"));
        String[] to = {"contact@aawaz.com.np", "dhakalramu2070@gmail.com"};
        sendMail.putExtra(Intent.EXTRA_EMAIL, to);
        sendMail.putExtra(Intent.EXTRA_SUBJECT, "Crash report of CSIT Entrance application.");
        sendMail.putExtra(Intent.EXTRA_TEXT,crashReportData);
        sendMail.setType("message/rfc822");
        Intent chooser = Intent.createChooser(sendMail, "Crash Report via E-mail");
        startActivity(chooser);
    }
}
}