package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.MenuClicks;


public class CollegesAdapter extends RecyclerView.Adapter<CollegesAdapter.ViewHolder> {
    ArrayList<String> address;
    Context context;
    LayoutInflater inflater;
    ArrayList<String> colleges;
    private MenuClicks menuClicks;

    public CollegesAdapter(Context context, ArrayList<String> colleges, ArrayList<String> address) {
        this.colleges = colleges;
        this.address = address;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.colz_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.colzName.setText(colleges.get(position));
        holder.address.setText(address.get(position));
    }

    @Override
    public int getItemCount() {
        return colleges.size();
    }


    public void setMenuClickListener(MenuClicks clickListener) {
        this.menuClicks = clickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView colzName, address;
        LinearLayout website, call, maps;

        public ViewHolder(View itemView) {
            super(itemView);
            colzName = (RobotoTextView) itemView.findViewById(R.id.colzName);
            address = (RobotoTextView) itemView.findViewById(R.id.address);
            website = (LinearLayout) itemView.findViewById(R.id.webSiteButton);
            call = (LinearLayout) itemView.findViewById(R.id.callButton);
            maps = (LinearLayout) itemView.findViewById(R.id.mapsButton);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuClicks.onCallClicked(getAdapterPosition());
                }
            });

            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuClicks.onWebsiteClicked(getAdapterPosition());
                }
            });

            maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuClicks.onMapClicked(getAdapterPosition());
                }
            });
        }
    }


}