package np.com.aawaz.csitentrance.adapters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;

public class ScoreBoardAdapter extends RecyclerView.Adapter<ScoreBoardAdapter.ViewHolder> {
    private ArrayList<Integer> scores;
    private ArrayList<String> names;
    private LayoutInflater inflater;
    private Context context;


    public ScoreBoardAdapter(Context context, ArrayList<String> names, ArrayList<Integer> scores) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.names = names;
        this.scores = scores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.score_board_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(names.get(position));
        holder.score.setText(scores.get(position) + "");
    }

    @Override
    public int getItemCount() {
        return names.size() > 10 ? 10 : names.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.scoreboardName);
            score = (TextView) itemView.findViewById(R.id.scoreboardScore);
        }
    }
}
