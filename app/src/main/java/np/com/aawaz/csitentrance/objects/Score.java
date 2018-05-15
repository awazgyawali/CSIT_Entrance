package np.com.aawaz.csitentrance.objects;

public class Score {

    public int q2069, q2070, q2071, q2072, q2073, q2074, model1, model2, model3, model4, model5, model6,
            math, physics, english, chemistry;

    public static Score getScoreObject() {
        Score score = new Score();
        score.q2069 = SPHandler.getInstance().getScore(SPHandler.YEAR2069);
        score.q2070 = SPHandler.getInstance().getScore(SPHandler.YEAR2070);
        score.q2071 = SPHandler.getInstance().getScore(SPHandler.YEAR2071);
        score.q2072 = SPHandler.getInstance().getScore(SPHandler.YEAR2072);
        score.q2073 = SPHandler.getInstance().getScore(SPHandler.YEAR2073);
        score.q2074 = SPHandler.getInstance().getScore(SPHandler.YEAR2074);

        score.model1 = SPHandler.getInstance().getScore(SPHandler.MODEL1);
        score.model2 = SPHandler.getInstance().getScore(SPHandler.MODEL2);
        score.model3 = SPHandler.getInstance().getScore(SPHandler.MODEL3);
        score.model4 = SPHandler.getInstance().getScore(SPHandler.MODEL4);
        score.model5 = SPHandler.getInstance().getScore(SPHandler.MODEL5);


        score.math = SPHandler.getInstance().getScore(SPHandler.MATH);
        score.physics = SPHandler.getInstance().getScore(SPHandler.PHYSICS);
        score.english = SPHandler.getInstance().getScore(SPHandler.ENGLISH);
        score.chemistry = SPHandler.getInstance().getScore(SPHandler.CHEMISTRY);
        return score;
    }

}
