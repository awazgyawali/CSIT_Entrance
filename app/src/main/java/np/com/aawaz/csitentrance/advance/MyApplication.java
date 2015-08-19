package np.com.aawaz.csitentrance.advance;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import np.com.aawaz.csitentrance.R;

@ReportsCrashes(mailTo = "csitentrance@gmail.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.ANDROID_VERSION,
        ReportField.PHONE_MODEL,ReportField.STACK_TRACE,ReportField.USER_COMMENT },
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast

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
        super.onCreate();
    }
}