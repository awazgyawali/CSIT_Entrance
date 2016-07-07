package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.objects.PremiumSet;

public class PremiumAdapter extends RecyclerView.Adapter<PremiumAdapter.VH> {
    private Context context;
    public ArrayList<PremiumSet> sets = new ArrayList<>();
    ClickListener clickListener;

    public PremiumAdapter(Context context) {
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.each_set_premium, parent, false));
    }

    public void addSet(PremiumSet set) {
        sets.add(set);
        notifyItemInserted(sets.size() - 1);
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.name.setText(sets.get(position).name);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return sets.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        RobotoTextView name;

        public VH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.itemClicked(view, getAdapterPosition());
                }
            });
            name = (RobotoTextView) itemView.findViewById(R.id.set_name);
        }
    }
}
