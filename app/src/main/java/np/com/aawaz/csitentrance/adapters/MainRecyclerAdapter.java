package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import np.com.aawaz.csitentrance.R;


public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {
    String[] titles;
    int primaryColor[];
    int darkColor[];
    int icon[];
    int images[];
    ClickListner clickListner;
    LayoutInflater inflater;
    Context context;
    SharedPreferences sharedPreferences;


    public MainRecyclerAdapter(Context context, int primaryColor[], int darkColor[], int icon[], String[] titles, int[] images) {
        inflater = LayoutInflater.from(context);
        this.darkColor = darkColor;
        this.primaryColor = primaryColor;
        this.icon = icon;
        this.context = context;
        this.titles = titles;
        this.images = images;
        sharedPreferences = context.getSharedPreferences("values", Context.MODE_PRIVATE);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.grid_item_resource, parent, false);
        return new ViewHolder(view);
    }

    public void setClickListner(ClickListner clickListner) {
        this.clickListner = clickListner;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mainImg.setImageResource(images[position]);
        YoYo.with(Techniques.FlipInX)
                .duration(500)
                .playOn(holder.mainLayout);
        holder.title.setText(titles[position]);
        holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(darkColor[position]));
        holder.baseLayout.setCardBackgroundColor(context.getResources().getColor(primaryColor[position]));
        holder.playImg.setImageResource(icon[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }


    public interface ClickListner {
        void itemClicked(View view, int position);
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

