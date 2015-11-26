package np.com.aawaz.csitentrance.advance;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.FacebookSdk;

import np.com.aawaz.csitentrance.R;

public class MyApplication extends Application {
    static EditText phoneNo;
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }


    public static void inputPhoneNo(final Context context) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Update your contact info.")
                .positiveText("Confirm")
                .negativeText("Cancel")
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (phoneNo.getText().toString().length() != 10) {
                            YoYo.with(Techniques.Shake)
                                    .duration(1000)
                                    .playOn(phoneNo);
                            Toast.makeText(context, "Invalid mobile number.", Toast.LENGTH_SHORT).show();
                            phoneNo.setText("");
                        } else {
                            context.getSharedPreferences("info", MODE_PRIVATE).edit().putString("PhoneNo", phoneNo.getText().toString() + "").apply();
                            Toast.makeText(context, "Thank you.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                })
                .customView(R.layout.enter_phone_no, false)
                .build();
        dialog.show();
        phoneNo = (EditText) dialog.findViewById(R.id.phoneNoText);
    }

    public static BitmapDrawable writeTextOnDrawable(Context context, String text) {

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.to_be_written)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#28AF84"));
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(context, 11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        paint.setTextSize(convertToPixels(context, 30));

        int xPos = (canvas.getWidth() / 3);

        int yPos = (int) ((canvas.getHeight() / 1.5));

        canvas.drawText(text, xPos, yPos, paint);

        return new BitmapDrawable(context.getResources(), bm);
    }

    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

}