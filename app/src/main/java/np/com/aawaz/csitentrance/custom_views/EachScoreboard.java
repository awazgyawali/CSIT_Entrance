package np.com.aawaz.csitentrance.custom_views;


import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class EachScoreboard {

    SPHandler spHandler;
    private View view;

    public EachScoreboard() {
        // Required empty public constructor
        spHandler = SPHandler.getInstance();
    }

    public EachScoreboard setView(View view) {
        this.view = view;
        return this;
    }

    public View getHeaderView() {

        ArcProgress phyArc = (ArcProgress) view.findViewById(R.id.arc_physics);
        ArcProgress chemArc = (ArcProgress) view.findViewById(R.id.arc_chem);
        ArcProgress mathArc = (ArcProgress) view.findViewById(R.id.arc_math);
        ArcProgress engArc = (ArcProgress) view.findViewById(R.id.arc_english);
        TextView totalScore = (TextView) view.findViewById(R.id.totalScoreScoreboard);

        phyArc.setProgress(spHandler.getAccuracy(SPHandler.PHYSICS));
        chemArc.setProgress(spHandler.getAccuracy(SPHandler.CHEMISTRY));
        mathArc.setProgress(spHandler.getAccuracy(SPHandler.MATH));
        engArc.setProgress(spHandler.getAccuracy(SPHandler.ENGLISH));
        totalScore.setText("Total: " + spHandler.getTotalScore());

        return view;
    }

    public View getCardView(int position) {
        String[] titles = MyApplication.getAppContext().getResources().getStringArray(R.array.years);
        String[] spTexts = {SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072, SPHandler.YEAR2073,SPHandler.YEAR2074,
                SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4, SPHandler.MODEL5, SPHandler.MODEL6};

        TextView title = (TextView) view.findViewById(R.id.titleScoreboardCard);
        TextView played = (TextView) view.findViewById(R.id.playedScoreboard);
        TextView score = (TextView) view.findViewById(R.id.scoreScoreboard);
        TextView accuracy = (TextView) view.findViewById(R.id.accuracyScoreboard);

        title.setText(titles[position]);
        played.setText(spHandler.getPlayed(spTexts[position]) + "");
        score.setText(spHandler.getScore(spTexts[position]) + "");
        accuracy.setText(spHandler.getAccuracy(spTexts[position]) + "%");

        return view;
    }
}
