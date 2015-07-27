package np.com.aawaz.csitentrance.Adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    String[] names,fbLink,twitterHandle,fbId;


    public AboutAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context=context;
        names = new String[]{"Aawaz Gyawali\nDeveloper", "Rameshwor Dhakal\nFeed writer",
                "Buddhiraj Nagarkoti\nGraphics Designer", "Prasanna Mishra\nUI Designer",
                "Gopal Nepal","Bishal Rana Magar","Ksitiz Khanal"};
        twitterHandle=new String[]{"AawazGyawali","RameshworDhakal","","Prascq56",
                "","",""};
        fbLink=new String[]{"https://www.facebook.com/muskangyawali","https://www.facebook.com/ranjit.sharma.39904",
                "https://www.facebook.com/Fcukyna","https://www.facebook.com/prasanna.mishra.79",
                "https://www.facebook.com/gopal.nepal.376","https://www.facebook.com/bishalr",
                "https://www.facebook.com/kshitij.khanal.5"};
        fbId=new String[]{"1011525682198565","669107896567041","823871820995107","949421761742563","803413353107871",
                "812230398798953","685992274877992"};
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.about_each_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(names[position]);

        if(!twitterHandle[position].equals("")) {
            holder.handle.setVisibility(View.VISIBLE);
            holder.handle.setText("@"+twitterHandle[position]);
        }
        else
            holder.handle.setVisibility(View.GONE);

        if(!fbLink[position].equals("")) {
            holder.facebook.setVisibility(View.VISIBLE);
            holder.facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    try {
                        context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/"+fbId[position]));
                    } catch (Exception e) {
                        i=new Intent(Intent.ACTION_VIEW, Uri.parse(fbLink[position]));
                    }

                    context.startActivity(i);
                }
            });
        }
        else
            holder.facebook.setVisibility(View.GONE);

        if(!twitterHandle[position].equals("")) {
            holder.twitter.setVisibility(View.VISIBLE);
            holder.twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.twitter.com/"+twitterHandle[position]));
                    context.startActivity(i);
                }
            });
        }
        else
            holder.twitter.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,handle;
        FancyButton twitter,facebook;

        public ViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.name);
            handle= (TextView) itemView.findViewById(R.id.twitterHandle);
            twitter= (FancyButton) itemView.findViewById(R.id.btn_twitter);
            facebook= (FancyButton) itemView.findViewById(R.id.btn_fb);
        }
    }
}
