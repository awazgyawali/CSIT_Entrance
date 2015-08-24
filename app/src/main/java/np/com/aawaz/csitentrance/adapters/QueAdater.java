package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;

public class QueAdater extends RecyclerView.Adapter<QueAdater.ViewHolder> {
    String[] titles = {"2069 Question", "2070 Question", "2071 Question", "Model Question 1", "Model Question 2", "Model Question 3"
            , "Model Question 4", "Model Question 5"};
    ClickListner clickListner;
    LayoutInflater inflater;
    Context context;

    public QueAdater(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.full_question_each_item, parent, false);
        return new ViewHolder(view);
    }

    public void setClickListner(ClickListner clickListner) {
        this.clickListner = clickListner;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }


    public interface ClickListner {
        void itemClicked(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListner.itemClicked(view, getAdapterPosition());
                }
            });
        }

    }
}


