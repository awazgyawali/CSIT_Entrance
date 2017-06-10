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

    private Context context;
    ArrayList<FAQ> faqs = new ArrayList<>();

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
    public void onBindViewHolder(VH holder, int position) {
        holder.answer.setText(faqs.get(position).answer);
        holder.question.setText(faqs.get(position).question);
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
