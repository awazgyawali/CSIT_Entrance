package np.com.aawaz.csitentrance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.EachScoreboard;
import np.com.aawaz.csitentrance.misc.MyApplication;

public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.VH> {

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new VH(LayoutInflater.from(MyApplication.getAppContext()).inflate(R.layout.scoreboard_header, parent, false));
        else
            return new VH(LayoutInflater.from(MyApplication.getAppContext()).inflate(R.layout.year_scoreboard, parent, false));

    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (position == 0)
            new EachScoreboard()
                    .setView(holder.coreView)
                    .getHeaderView();
        else
            new EachScoreboard()
                    .setView(holder.coreView)
                    .getCardView(position - 1);
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class VH extends RecyclerView.ViewHolder {
        View coreView;

        public VH(View itemView) {
            super(itemView);
            coreView = itemView;
        }
    }
}
