package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.FullQuestionActivity;
import np.com.aawaz.csitentrance.activities.ModelPaperDetailActivity;
import np.com.aawaz.csitentrance.activities.YearQuizActivity;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.objects.YearItem;

public class YearListAdapter extends RecyclerView.Adapter<YearListAdapter.VH> {


    private final Context context;
    private final ArrayList<YearItem> items;

    private String[] codes = {SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072, SPHandler.YEAR2073, SPHandler.YEAR2074,
            SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4, SPHandler.MODEL5, SPHandler.MODEL6, SPHandler.MODEL7,
            SPHandler.MODEL8, SPHandler.MODEL9, SPHandler.MODEL10, SPHandler.SAGARMATHA, SPHandler.MODEL12, SPHandler.MODEL13, SPHandler.MODEL14};

    public YearListAdapter(Context context, ArrayList<YearItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == YearItem.Companion.getSECTION_TITLE())
            v = LayoutInflater.from(context).inflate(R.layout.year_header, parent, false);
        else if (viewType == YearItem.Companion.getCOLLEGE_MODEL())
            v = LayoutInflater.from(context).inflate(R.layout.college_model_question, parent, false);
        else if (viewType == YearItem.Companion.getYEAR_SET())
            v = LayoutInflater.from(context).inflate(R.layout.each_year_quiz_card, parent, false);
        return new VH(v);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final YearItem item = items.get(position);
        holder.title.setText(item.getTitle());

        if (item.getType() == YearItem.Companion.getYEAR_SET()) {
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openQuizQuestion(item.getPaperCode());
                }
            });
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFullQuestion(item.getPaperCode());
                }
            });
        } else if (item.getType() == YearItem.Companion.getCOLLEGE_MODEL()) {
            holder.logo.setImageResource(item.getLogo());
            holder.core.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ModelPaperDetailActivity.class)
                            .putExtra("college", item.getTitle()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void openFullQuestion(int position) {
        Intent intent = new Intent(context, FullQuestionActivity.class);
        intent.putExtra("code", codes[position]);
        intent.putExtra("position", position + 1);
        new EventSender().logEvent("viewed_full");

        context.startActivity(intent);
    }

    public void openQuizQuestion(int position) {
        Intent intent = new Intent(context, YearQuizActivity.class);

        intent.putExtra("code", codes[position]);
        intent.putExtra("position", position);
        new EventSender().logEvent("played_year");

        context.startActivity(intent);
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title;
        TextView address;
        ImageView logo;
        TextView play, view;
        View core;

        VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);
            play = itemView.findViewById(R.id.year_play);
            view = itemView.findViewById(R.id.year_view);
            logo = itemView.findViewById(R.id.modelAdImage);
            core = itemView;
        }
    }
}
