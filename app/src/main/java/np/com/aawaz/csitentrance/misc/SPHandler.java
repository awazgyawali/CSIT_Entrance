package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.content.SharedPreferences;

public class SPHandler {
    private final SharedPreferences.Editor editor;
    SharedPreferences preferences;

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
        preferences = MyApplication.getAppContext().getSharedPreferences("play_data", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public int getScore(String name) {
        return preferences.getInt(name + SCORE, 0);
    }

    public int getPlayed(String name) {
        return preferences.getInt(name + PLAYED, 0);
    }

    public void setScore(String name, int value) {
        editor.putInt(name + SCORE, value).apply();
    }

    public void setPlayed(String name, int value) {
        editor.putInt(name + PLAYED, value).apply();
    }

    public void increasePlayed(String name) {
        editor.putInt(name + PLAYED, getPlayed(name) + 1).apply();
    }

    public void increaseScore(String name) {
        editor.putInt(name + SCORE, getPlayed(name) + 1).apply();
    }

    public int getAccuracy(String name) {
        return (int) (((float) getScore(name) / (float) getPlayed(name)) * 100);
    }

    public int getTotalScore() {
        return getScore(YEAR2069) + getScore(YEAR2070) + getScore(YEAR2071) + getScore(YEAR2072) +
                getScore(MODEL1) + getScore(MODEL2) + getScore(MODEL3) + getScore(MODEL4);
    }
}
