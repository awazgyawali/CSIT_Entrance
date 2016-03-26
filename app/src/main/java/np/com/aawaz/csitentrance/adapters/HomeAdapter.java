package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.ClickListener;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    String[] titles;
    int primaryColor[];
    int darkColor[];
    int icon[];
    int images[];
    ClickListener clickListner;
    LayoutInflater inflater;
    Context context;
    SharedPreferences sharedPreferences;


    public HomeAdapter(Context context, String[] titles) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.titles = titles;
        sharedPreferences = context.getSharedPreferences("values", Context.MODE_PRIVATE);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.home_each_item, parent, false);
        return new ViewHolder(view);
    }

    public void setClickListener(ClickListener clickListner) {
        this.clickListner = clickListner;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mainImg.setImageResource(images[position]);
        holder.title.setText(titles[position]);
        holder.mainLayout.setCardBackgroundColor(ContextCompat.getColor(context, darkColor[position]));
        holder.baseLayout.setCardBackgroundColor(ContextCompat.getColor(context, primaryColor[position]));
        holder.playImg.setImageResource(icon[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mainImg;
        ImageView playImg;
        TextView title;
        CardView mainLayout;
        CardView baseLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mainImg = (ImageView) itemView.findViewById(R.id.mainImg);
            playImg = (ImageView) itemView.findViewById(R.id.play);
            mainLayout = (CardView) itemView.findViewById(R.id.mainLayout);
            baseLayout = (CardView) itemView.findViewById(R.id.baseLayout);
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

