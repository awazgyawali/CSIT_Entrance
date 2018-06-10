package np.com.aawaz.csitentrance.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.ProfileActivity;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Long> scores;
    private ArrayList<String> uids;
    private ArrayList<String> names;
    private ArrayList<String> image_url;
    private LayoutInflater inflater;


    public LeaderboardAdapter(Context context, ArrayList<String> uids, ArrayList<String> names, ArrayList<Long> scores, ArrayList<String> image_url) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.uids = uids;
        this.names = names;
        this.scores = scores;
        this.image_url = image_url;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.leader_board_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(names.get(position));
        holder.score.setText(scores.get(position) + "");
        Picasso.with(context)
                .load(image_url.get(position))
                .placeholder(R.drawable.account_holder)
                .into(holder.image);
        holder.numbering.setText(position + 4 + ". ");
        holder.core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", uids.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size() > 7 ? 7 : names.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, score, numbering;
        CircleImageView image;
        View core;

        ViewHolder(View itemView) {
            super(itemView);
            core = itemView;
            numbering = itemView.findViewById(R.id.numbering);
            name = itemView.findViewById(R.id.scoreboardName);
            score = itemView.findViewById(R.id.scoreboardScore);
            image = itemView.findViewById(R.id.leaderboard_item_image);
        }
    }
}
