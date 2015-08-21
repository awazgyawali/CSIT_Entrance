package np.com.aawaz.csitentrance.advance;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

@ReportsCrashes(customReportContent = {ReportField.APP_VERSION_CODE, ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.USER_COMMENT},
        mode = ReportingInteractionMode.SILENT
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
        YourOwnSender yourSender = new YourOwnSender();
        ACRA.getErrorReporter().setReportSender(yourSender);
        super.onCreate();
    }

    public class YourOwnSender implements ReportSender {

        public YourOwnSender() {
        }

        @Override
        public void send(Context context, CrashReportData crashReportData) throws ReportSenderException {
            startActivity(new Intent(context, CrashReportSender.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("CrashDetail", crashReportData.toString()));
        }
    }
}