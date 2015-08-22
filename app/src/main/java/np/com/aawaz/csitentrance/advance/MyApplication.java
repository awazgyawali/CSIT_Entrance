package np.com.aawaz.csitentrance.advance;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.util.JSONReportBuilder;

@ReportsCrashes(customReportContent = {ReportField.APP_VERSION_CODE, ReportField.ANDROID_VERSION,
        ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.USER_COMMENT},
        mode = ReportingInteractionMode.SILENT)
public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
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
            ContentValues values = new ContentValues();
            SQLiteDatabase database = Singleton.getInstance().getDatabase();
            try {
                values.put("text", crashReportData.toJSON().toString());
            } catch (JSONReportBuilder.JSONReportException ignored) {

            }
            database.insert("report", null, values);
            database.close();
        }
    }
}