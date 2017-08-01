package np.com.aawaz.csitentrance.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.MyApplication;

public class SPHandler {
    public static String CHEMISTRY = "chem";
    public static String PHYSICS = "phy";
    public static String MATH = "math";
    public static String ENGLISH = "eng";
    public static String YEAR2069 = "year2069";
    public static String YEAR2070 = "year2070";
    public static String YEAR2071 = "year2071";
    public static String YEAR2072 = "year2072";
    public static String YEAR2073 = "year2073";
    public static String MODEL1 = "model1";
    public static String MODEL2 = "model2";
    public static String MODEL3 = "model3";
    public static String MODEL4 = "model4";
    public static String MODEL5 = "model5";
    private static SPHandler spHandler;
    private final SharedPreferences.Editor scoreEditor;
    private final SharedPreferences.Editor infoEditor;
    SharedPreferences scoreSp;
    SharedPreferences infoSp;
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
        new EventSender().logEvent("questions_played");
        scoreEditor.putInt(name + PLAYED, getPlayed(name) + 1).apply();
    }

    public void increaseScore(String name) {
        scoreEditor.putInt(name + SCORE, getScore(name) + 1).apply();
        setScoreChanged(true);
    }

    public boolean isScoreChanged() {
        return scoreSp.getBoolean("is_score_changed", true);
    }

    public void setScoreChanged(boolean changed) {
        scoreEditor.putBoolean("is_score_changed", changed).apply();
    }

    public int getAccuracy(String name) {
        return (int) (((float) getScore(name) / (float) getPlayed(name)) * 100);
    }

    public int getTotalScore() {
        return getScore(YEAR2069) + getScore(YEAR2070) + getScore(YEAR2071) + getScore(YEAR2072) + getScore(YEAR2073) +
                getScore(MODEL1) + getScore(MODEL2) + getScore(MODEL3) + getScore(MODEL4) + getScore(MODEL5);
    }

    public int getTotalPlayed() {
        return getPlayed(YEAR2069) + getPlayed(YEAR2070) + getPlayed(YEAR2071) + getPlayed(YEAR2072) + getPlayed(YEAR2073) +
                getPlayed(MODEL1) + getPlayed(MODEL2) + getPlayed(MODEL3) + getPlayed(MODEL4) + getPlayed(MODEL5);
    }

    public boolean isResultPublished() {
        return infoSp.getBoolean("result_publish", false);
    }

    public void setResultPublished() {
        infoEditor.putBoolean("result_publish", true).apply();
    }

    public String getSubjectCode(int index, int questionNo) {
        String[][] subjects = {new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{PHYSICS, ENGLISH, MATH, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{PHYSICS, CHEMISTRY, ENGLISH, MATH},
                new String[]{ENGLISH, PHYSICS, CHEMISTRY, MATH},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{ENGLISH, MATH, CHEMISTRY, PHYSICS}};
        return subjects[index][questionNo / 25];
    }

    public int getIndexOfQuestion(int index, String subject) {

        String[][] subjects = {new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{PHYSICS, ENGLISH, MATH, CHEMISTRY},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{PHYSICS, CHEMISTRY, ENGLISH, MATH},
                new String[]{ENGLISH, PHYSICS, CHEMISTRY, MATH},
                new String[]{MATH, ENGLISH, PHYSICS, CHEMISTRY},
                new String[]{ENGLISH, MATH, CHEMISTRY, PHYSICS}};
        for (int i = 0; i < 4; i++)
            if (subjects[index][i].equals(subject))
                return i * 25;
        return -1;
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

    public long getLastPostedTime() {
        return scoreSp.getLong("lastPosted", 0);
    }

    public void setLastPostedTime(long time) {
        scoreEditor.putLong("lastPosted", time).apply();
    }

    public String getForumText() {
        return scoreSp.getString("forumText", "");
    }

    public void setForumText(String forumText) {
        scoreEditor.putString("forumText", forumText).apply();
    }

    public boolean getForumSubscribed() {
        return scoreSp.getBoolean("forumSub", true);
    }

    public void setForumSubscribed(boolean newsSubscribed) {
        scoreEditor.putBoolean("forumSub", newsSubscribed).apply();
    }

    public boolean getNewsSubscribed() {
        return scoreSp.getBoolean("newsSub", true);
    }

    public void setNewsSubscribed(boolean newsSubscribed) {
        scoreEditor.putBoolean("newsSub", newsSubscribed).apply();
    }

    public void clearAll() {
        scoreEditor.clear().apply();
    }

    public String getLeaderBoardLastResponse() {
        return infoSp.getString("leaderboard", null);
    }

    public void setLeaderBoardLastResponse(String leaderBoardLastResponse) {
        infoEditor.putString("leaderboard", leaderBoardLastResponse).apply();
    }

    public String getPhoneNo() {
        return infoSp.getString("phone_no", "");
    }

    public void setPhoneNo(String no) {
        infoEditor.putString("phone_no", no).apply();
    }

    public void increaseTimesPlayed() {
        infoEditor.putInt("times_played", getTimesPlayed() + 1).commit();
    }

    public int getTimesPlayed() {
        return infoSp.getInt("times_played", 2);
    }

    public boolean isInstanceIdAdded() {
        return infoSp.getBoolean("instance_id", false);
    }

    public void instanceIdAdded() {
        infoEditor.putBoolean("instance_id", true).apply();
    }

    public int getLastAdPosition() {
        return infoSp.getInt("last_ad", 0);
    }

    public void setLastAd(int lastAd) {
        infoEditor.putInt("last_ad", lastAd).apply();
    }

    public void showAnswer(boolean isChecked) {
        infoEditor.putBoolean("answerShow", isChecked).apply();
    }

    public boolean shouldShowAnswers() {
        return infoSp.getBoolean("answerShow", true);
    }
}
