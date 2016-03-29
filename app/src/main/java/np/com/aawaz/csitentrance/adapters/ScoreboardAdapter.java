package np.com.aawaz.csitentrance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.MyApplication;

public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.VH> {

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new VH(View.inflate(MyApplication.getAppContext(), R.layout.scoreboard_header, null));
        else
            return new VH(View.inflate(MyApplication.getAppContext(), R.layout.year_scoreboard, null));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 9;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }
}
