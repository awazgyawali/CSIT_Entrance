package np.com.aawaz.csitentrance.advance;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.FacebookSdk;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import np.com.aawaz.csitentrance.R;

@ReportsCrashes(mailTo = "csitentrance@gmail.com",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.ANDROID_VERSION,
        ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.USER_COMMENT},
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast)
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

    public static void changeStatusBarColor(int colorResource, AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(colorResource));
        }
    }

    @Override
    public void onCreate() {
        sInstance = this;
        ACRA.init(this);
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }
}