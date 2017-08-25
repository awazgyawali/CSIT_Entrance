package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.FAQ;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.VH> {

    ArrayList<FAQ> faqs = new ArrayList<>();
    private Context context;
    private int expandedItem = -1;

    public FAQAdapter(Context context) {
        this.context = context;
    }

    public void add(FAQ faq) {
        faqs.add(faq);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.each_faq_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        FAQ faq = faqs.get(position);
        holder.answer.setText(faq.answer);
        holder.question.setText((position + 1) + ". " + faq.question);

        holder.question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandedItem == holder.getAdapterPosition()) {
                    holder.answer.setVisibility(View.GONE);
                    expandedItem = -1;
                } else if (expandedItem == -1) {
                    holder.answer.setVisibility(View.VISIBLE);
                    expandedItem = holder.getAdapterPosition();
                } else {
                    int tempExpandedItem = expandedItem;
                    expandedItem = position;
                    holder.answer.setVisibility(View.VISIBLE);
                    notifyItemChanged(tempExpandedItem);
                }
            }
        });

        if (expandedItem == position) {
            holder.answer.setVisibility(View.VISIBLE);
        } else {
            holder.answer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView question, answer;

        public VH(View itemView) {
            super(itemView);
            question = (TextView) itemView.findViewById(R.id.question);
            answer = (TextView) itemView.findViewById(R.id.answer);
        }
    }
}
