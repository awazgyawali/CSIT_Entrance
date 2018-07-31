package np.com.aawaz.csitentrance.objects

class Score {

    var q2069: Int = 0
    var q2070: Int = 0
    var q2071: Int = 0
    var q2072: Int = 0
    var q2073: Int = 0
    var q2074: Int = 0
    var model1: Int = 0
    var model2: Int = 0
    var model3: Int = 0
    var model4: Int = 0
    var model5: Int = 0
    var model6: Int = 0
    var samriddhi7: Int = 0
    var model8: Int = 0
    var model9: Int = 0
    var model10: Int = 0
    var sagarmatha11: Int = 0
    var model12: Int = 0
    var math: Int = 0
    var physics: Int = 0
    var english: Int = 0
    var chemistry: Int = 0

    companion object {
        val scoreObject: Score
            get() {
                val score = Score()

                score.samriddhi7 = SPHandler.getInstance().getScore(SPHandler.MODEL7)

                score.q2069 = SPHandler.getInstance().getScore(SPHandler.YEAR2069)
                score.q2070 = SPHandler.getInstance().getScore(SPHandler.YEAR2070)
                score.q2071 = SPHandler.getInstance().getScore(SPHandler.YEAR2071)
                score.q2072 = SPHandler.getInstance().getScore(SPHandler.YEAR2072)
                score.q2073 = SPHandler.getInstance().getScore(SPHandler.YEAR2073)
                score.q2074 = SPHandler.getInstance().getScore(SPHandler.YEAR2074)

                score.model1 = SPHandler.getInstance().getScore(SPHandler.MODEL1)
                score.model2 = SPHandler.getInstance().getScore(SPHandler.MODEL2)
                score.model3 = SPHandler.getInstance().getScore(SPHandler.MODEL3)
                score.model4 = SPHandler.getInstance().getScore(SPHandler.MODEL4)
                score.model5 = SPHandler.getInstance().getScore(SPHandler.MODEL5)
                score.model6 = SPHandler.getInstance().getScore(SPHandler.MODEL6)
                score.model8 = SPHandler.getInstance().getScore(SPHandler.MODEL8)
                score.model9 = SPHandler.getInstance().getScore(SPHandler.MODEL9)
                score.model10 = SPHandler.getInstance().getScore(SPHandler.MODEL10)
                score.sagarmatha11 = SPHandler.getInstance().getScore(SPHandler.SAGARMATHA)
                score.model12 = SPHandler.getInstance().getScore(SPHandler.MODEL12)


                score.math = SPHandler.getInstance().getScore(SPHandler.MATH)
                score.physics = SPHandler.getInstance().getScore(SPHandler.PHYSICS)
                score.english = SPHandler.getInstance().getScore(SPHandler.ENGLISH)
                score.chemistry = SPHandler.getInstance().getScore(SPHandler.CHEMISTRY)
                return score
            }
    }

}
