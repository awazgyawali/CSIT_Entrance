package np.com.aawaz.csitentrance.Adapters;

import android.content.Context;
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

public class QueAdater extends RecyclerView.Adapter<QueAdater.ViewHolder> {
    String[] titles = {"2069 Question", "2070 Question", "2071 Question", "Model Question"};
    int primaryColor[];
    int darkColor[];
    int icon[];
    ClickListner clickListner;
    LayoutInflater inflater;
    Context context;
    int lastPosi = 10;

    public QueAdater(Context context, int primaryColor[], int darkColor[], int icon[]) {
        inflater = LayoutInflater.from(context);
        this.darkColor = darkColor;
        this.primaryColor = primaryColor;
        this.icon = icon;
        this.context = context;
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

        YoYo.with(Techniques.Landing)
                .duration(500)
                .playOn(holder.mainLayout);
        lastPosi = position;
        holder.title.setText(titles[position]);
        holder.mainImg.setImageResource(R.drawable.full_questions);
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


