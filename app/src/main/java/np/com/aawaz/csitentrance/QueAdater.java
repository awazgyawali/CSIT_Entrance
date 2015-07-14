package np.com.aawaz.csitentrance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class QueAdater extends RecyclerView.Adapter<QueAdater.ViewHolder>{
    String[] titles={"2069 Question","2070 Question","2071 Question","Model Question"};
    int primaryColor[];
    int darkColor[];
    int icon[];
    ClickListner clickListner;
    LayoutInflater inflater;
    Context context;
    int lastPosi=10;
    QueAdater (Context context, int primaryColor[], int darkColor[], int icon[]){
        inflater = LayoutInflater.from(context);
        this.darkColor=darkColor;
        this.primaryColor=primaryColor;
        this.icon=icon;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.grid_item_resource, parent, false);
        return new ViewHolder(view);
    }

    public void setClickListner(ClickListner clickListner){
        this.clickListner=clickListner;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        YoYo.with(Techniques.Landing)
                .duration(500)
                .playOn(holder.mainLayout);
        lastPosi=position;
        if(position==2 || position==3)
            holder.title.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
        else
            holder.title.setTextColor(context.getResources().getColor(R.color.darkQue));
        holder.title.setText(titles[position]);
        holder.mainLayout.setBackgroundColor(context.getResources().getColor(darkColor[position]));
        holder.baseLayout.setBackgroundColor(context.getResources().getColor(primaryColor[position]));
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
        RelativeLayout mainLayout;
        RelativeLayout baseLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mainImg= (ImageView) itemView.findViewById(R.id.mainImg);
            playImg= (ImageView) itemView.findViewById(R.id.play);
            mainLayout= (RelativeLayout) itemView.findViewById(R.id.mainLayout);
            baseLayout= (RelativeLayout) itemView.findViewById(R.id.baseLayout);
            title= (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListner.itemClicked(view,getPosition());
                }
            });
        }

    }

    public interface ClickListner{
        void itemClicked(View view,int position);
    }
}


