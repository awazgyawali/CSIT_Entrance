package np.com.aawaz.csitentrance.Adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;

public class ScoreBoardAdapter extends RecyclerView.Adapter<ScoreBoardAdapter.ViewHolder> {
    ArrayList<Integer> scores;
    ArrayList<String> names;
    LayoutInflater inflater;
    int primaryColors[];
    int darkColors[];
    private Context context;


    public ScoreBoardAdapter(Context context, ArrayList<String> names, ArrayList<Integer> scores) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.names = names;
        this.scores = scores;
        this.primaryColors = new int[]{R.color.primary1, R.color.primary2, R.color.primary3, R.color.primary4, R.color.primary5,
                R.color.primary6, R.color.primary7, R.color.primary8, R.color.primary9, R.color.primary10,
                R.color.primary11};

        this.darkColors = new int[]{R.color.dark1, R.color.dark2, R.color.dark3, R.color.dark4, R.color.dark5,
                R.color.dark6, R.color.dark7, R.color.dark8, R.color.dark9, R.color.dark10,
                R.color.dark11};

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.score_board_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        YoYo.with(Techniques.FlipInX)
                .duration(700)
                .playOn(holder.upper);

        holder.name.setText(names.get(position));
        holder.score.setText(scores.get(position) + "");
        holder.upper.setCardBackgroundColor(context.getResources().getColor(darkColors[position]));
        holder.lower.setCardBackgroundColor(context.getResources().getColor(primaryColors[position]));
    }

    @Override
    public int getItemCount() {
        return names.size() > 10 ? 10 : names.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView score;
        CardView upper;
        CardView lower;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.scoreboardName);
            score = (TextView) itemView.findViewById(R.id.scoreboardScore);
            upper = (CardView) itemView.findViewById(R.id.upperLayout);
            lower = (CardView) itemView.findViewById(R.id.lowerLayout);
        }
    }
}
