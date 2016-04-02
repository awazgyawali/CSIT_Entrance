package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import np.com.aawaz.csitentrance.R;

public class SPHandler {
    private static SPHandler spHandler;
    private final SharedPreferences.Editor scoreEditor;
    SharedPreferences scoreSp;
    private final SharedPreferences.Editor infoEditor;
    SharedPreferences infoSp;

    public static String CHEMISTRY = "chem";
    public static String PHYSICS = "phy";
    public static String MATH = "math";
    public static String ENGLISH = "eng";

    public static String YEAR2069 = "year2069";
    public static String YEAR2070 = "year2070";
    public static String YEAR2071 = "year2071";
    public static String YEAR2072 = "year2072";

    public static String MODEL1 = "model1";
    public static String MODEL2 = "model2";
    public static String MODEL3 = "model3";
    public static String MODEL4 = "model4";

    private String PLAYED = "_played";
    private String SCORE = "_score";

    public SPHandler() {
        scoreSp = MyApplication.getAppContext().getSharedPreferences("play_data", Context.MODE_PRIVATE);
        scoreEditor = scoreSp.edit();

        infoSp = MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        infoEditor = infoSp.edit();
    }

    public static SPHandler getInstance() {
        if (spHandler == null)
            return spHandler = new SPHandler();
        else
            return spHandler;
    }

    public int getScore(String name) {
        return scoreSp.getInt(name + SCORE, 0);
    }

    public int getPlayed(String name) {
        return scoreSp.getInt(name + PLAYED, 0);
    }

    public void setScore(String name, int value) {
        scoreEditor.putInt(name + SCORE, value).apply();
    }

    public void setPlayed(String name, int value) {
        scoreEditor.putInt(name + PLAYED, value).apply();
    }

    public void increasePlayed(String name) {
        scoreEditor.putInt(name + PLAYED, getPlayed(name) + 1).apply();
    }

    public void increaseScore(String name) {
        scoreEditor.putInt(name + SCORE, getScore(name) + 1).apply();
    }

    public int getAccuracy(String name) {
        return (int) (((float) getScore(name) / (float) getPlayed(name)) * 100);
    }

    public int getTotalScore() {
        return getScore(YEAR2069) + getScore(YEAR2070) + getScore(YEAR2071) + getScore(YEAR2072) +
                getScore(MODEL1) + getScore(MODEL2) + getScore(MODEL3) + getScore(MODEL4);
    }

    public String getFullName() {
        return infoSp.getString("First-Name", "") + " " + infoSp.getString("Last-Name", "");
    }

    public String getEmail() {
        return infoSp.getString("E-Mail", "");
    }

    public String getImageLink() {
        return infoSp.getString("ImageLink", null);
    }

    public boolean isLoggedIn() {
        return infoSp.getBoolean("LoggedIn", false);
    }

    public void setLoggedIn() {
        infoEditor.putBoolean("LoggedIn", true).apply();
    }

    public boolean isResultPublished() {
        return infoSp.getBoolean("published", false);
    }

    public void setResultPublished() {
        infoEditor.putBoolean("published", true).apply();
    }

    public String getID() {
        return infoSp.getString("UserID", "");
    }

    public boolean isSocialLoggedIn() {
        return infoSp.getBoolean("socialLogged", false);
    }

    public void setSocialLoggedIn() {
        infoEditor.putBoolean("socialLogged", true).apply();
    }

    public void saveLoginData(String firstname, String surname, String email, String phone) {
        infoEditor.putString("First-Name", firstname)
                .putString("Last-Name", surname)
                .putString("E-Mail", email)
                .putString("Phone", phone)
                .apply();
    }

    public void setImageLink(String link) {
        infoEditor.putString("ImageLink", link).apply();
    }

    public String getSubjectCode(int index, int questionNo) {
        String[][] subjects = {new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{ENGLISH, MATH, CHEMISTRY, PHYSICS},
                new String[]{PHYSICS, ENGLISH, MATH, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{PHYSICS, CHEMISTRY, ENGLISH, MATH},
                new String[]{ENGLISH, PHYSICS, CHEMISTRY, MATH}};
        return subjects[index][questionNo / 25];
    }

    public int getSubjectColor(String subCode) {
        switch (subCode) {
            case "math":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.math);
            case "phy":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.physics);
            case "chem":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.chemistry);
            case "eng":
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.english);
            default:
                return ContextCompat.getColor(MyApplication.getAppContext(), R.color.colorPrimary);

        }
    }

    public String getSubjectName(String subCode) {
        switch (subCode) {
            case "math":
                return "Mathematics";
            case "phy":
                return "Physics";
            case "chem":
                return "Chemistry";
            case "eng":
                return "English";
            default:
                return "";
        }
    }
}
