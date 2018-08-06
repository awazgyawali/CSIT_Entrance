package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.SubjectQuizActivity;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.objects.YearItem;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.VH> {


    private final Context context;
    private String subject;
    private final ArrayList<YearItem> items;

    public SubjectListAdapter(Context context, String subject, ArrayList<YearItem> items) {
        this.context = context;
        this.subject = subject;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == YearItem.Companion.getSECTION_TITLE())
            v = LayoutInflater.from(context).inflate(R.layout.year_header, parent, false);
        else if (viewType == YearItem.Companion.getYEAR_SET())
            v = LayoutInflater.from(context).inflate(R.layout.each_subject_chooser_item, parent, false);
        return new VH(v);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        final YearItem item = items.get(position);
        holder.title.setText(item.getTitle());

        if (item.getType() == YearItem.Companion.getYEAR_SET()) {
            holder.core.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(item.getPaperCode());
                }
            });

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(item.getPaperCode());
                }
            });
        }
    }

    private void clicked(int pos) {
        String[] codes = {SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072, SPHandler.YEAR2073, SPHandler.YEAR2074,
                SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4, SPHandler.MODEL5, SPHandler.MODEL6, SPHandler.MODEL7,
                SPHandler.MODEL8, SPHandler.MODEL9, SPHandler.MODEL10, SPHandler.SAGARMATHA, SPHandler.MODEL12};
        Intent intent = new Intent(context, SubjectQuizActivity.class);
        intent.putExtra("code", codes[pos]);
        intent.putExtra("position", pos);
        intent.putExtra("subject", subject);
        context.startActivity(intent);
        new EventSender().logEvent("played_subject");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title;
        View core;

        VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            core = itemView;
        }
    }
}
