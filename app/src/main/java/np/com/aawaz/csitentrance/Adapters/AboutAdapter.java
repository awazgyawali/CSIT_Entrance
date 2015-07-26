package np.com.aawaz.csitentrance.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
    LayoutInflater inflater;
    String[] names;


    public AboutAdapter(Context context, String[] names) {
        inflater = LayoutInflater.from(context);
        this.names = names;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.about_each_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(names[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.name);
        }
    }
}
