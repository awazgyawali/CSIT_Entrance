package np.com.aawaz.csitentrance.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;


public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
    private static int type_header = 0;
    private static int type_normal = 1;
    private static int type_footer = 2;
    LayoutInflater inflater;
    Context context;
    String[] names,fbLink,twitterHandle,fbId,twiterUserId;


    public AboutAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context=context;
        names = new String[]{"Aawaz Gyawali\nLead Developer", "Roshan Gautam\nDeveloper", "Rameshwor Dhakal\nFeed writer",
                "Buddhiraj Nagarkoti\nGraphics Designer", "Prasanna Mishra\nUI Designer",
                "Gopal Nepal", "Ksitiz Khanal"};
        twitterHandle = new String[]{"AawazGyawali", "roshangautam0", "RameshworDhakal", "13rains", "Prascq56",
                "Meet2Gopal", "kshitij_khanal"};
        twiterUserId = new String[]{"2960523083", "995677063", "3080519719", "", "2475260587", "953087550", "2940948572"};
        fbLink = new String[]{"muskangyawali", "roshangm1", "ranjit.sharma.39904",
                "Fcukyna", "prasanna.mishra.79",
                "gopal.nepal.376", "kshitij.khanal.5"};
        fbId = new String[]{"100000234287319", "100001110021317", "100004035110394", "100001168909448", "100000242453492",
                "100003176412331", "100004016767543"};
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == type_footer)
            return new ViewHolder(inflater.inflate(R.layout.about_footer, parent, false));
        else if (viewType == type_header)
            return new ViewHolder(inflater.inflate(R.layout.csitan_intro, parent, false));
        return new ViewHolder(inflater.inflate(R.layout.about_each_item, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return type_footer;
        else if (position == 0)
            return type_header;
        return type_normal;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == getItemCount() - 1 || position == 0) {
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500)
                    .playOn(holder.aboutExtreme);
            return;
        }
        if (position % 2 == 0)
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500)
                    .playOn(holder.aboutCore);
        else
            YoYo.with(Techniques.FadeInRight)
                    .duration(500)
                    .playOn(holder.aboutCore);
        final int newPosition = position - 1;
        holder.name.setText(names[newPosition]);
        if (!twitterHandle[newPosition].equals("")) {
            holder.handle.setVisibility(View.VISIBLE);
            holder.handle.setText("@" + twitterHandle[newPosition]);
        }
        else
            holder.handle.setVisibility(View.GONE);

        if (!fbLink[newPosition].equals("")) {
            holder.facebook.setVisibility(View.VISIBLE);
            holder.facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = null;
                    try {
                        context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + fbId[newPosition]));
                    } catch (Exception e) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + fbLink[newPosition]));
                    } finally {
                        context.startActivity(i);
                    }
                }
            });
        }
        else
            holder.facebook.setVisibility(View.GONE);
        if (!twitterHandle[newPosition].equals("")) {
            holder.twitter.setVisibility(View.VISIBLE);
            holder.twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = null;
                    try {
                        context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + twiterUserId[newPosition]));
                    } catch (Exception e) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twitterHandle[newPosition]));
                    } finally {
                        context.startActivity(i);
                    }
                }
            });
        }
        else
            holder.twitter.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return names.length + 2;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,handle;
        FancyButton twitter,facebook;
        FrameLayout aboutCore;
        CardView aboutExtreme;

        public ViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.name);
            handle= (TextView) itemView.findViewById(R.id.twitterHandle);
            twitter= (FancyButton) itemView.findViewById(R.id.btn_twitter);
            facebook= (FancyButton) itemView.findViewById(R.id.btn_fb);
            aboutCore = (FrameLayout) itemView.findViewById(R.id.about_each_core);
            aboutExtreme = (CardView) itemView.findViewById(R.id.about_extreme);
        }
    }
}
