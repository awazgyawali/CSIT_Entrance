package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;


public class CollegesAdapter extends RecyclerView.Adapter<CollegesAdapter.ViewHolder> {
    ArrayList<String> desc;
    ArrayList<String> website;
    ArrayList<String> phNo;
    ArrayList<String> address;
    Context context;
    LayoutInflater inflater;
    ArrayList<String> colleges;
    int lastPosi=0;

    public CollegesAdapter(Context context, ArrayList<String> colleges, ArrayList<String> address, ArrayList<String> desc, ArrayList<String> website, ArrayList<String> phNo) {
        this.colleges = colleges;
        this.address = address;
        this.desc = desc;
        this.website = website;
        this.phNo = phNo;
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
        lastPosi=position;
        holder.colzName.setText(colleges.get(position));
        holder.address.setText(address.get(position));
        if (website.get(position).equals("null")) {
            holder.website.setVisibility(View.GONE);
        } else {
            holder.website.setVisibility(View.VISIBLE);
            holder.website.setText(website.get(position));
        }
        holder.coreColz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!website.get(position).equals("null")) {
                    new MaterialDialog.Builder(context)
                            .title("Confirmation.")
                            .content("Open " + website.get(position) + "?")
                            .positiveText("Open")
                            .negativeText("Cancel")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website.get(position)));
                                    context.startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });
        if (desc.get(position).equals("null")) {
            holder.desc.setVisibility(View.GONE);
        } else {
            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.setText(desc.get(position));
        }
        if (phNo.get(position).equals("null"))
            holder.call.setVisibility(View.GONE);
        else
            holder.call.setVisibility(View.VISIBLE);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("Call Confirmation")
                        .content("Call " + phNo.get(position) + "?")
                        .positiveText("Call")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phNo.get(position), null));
                                context.startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return colleges.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView colzName;
        TextView website;
        TextView address;
        TextView desc;
        FrameLayout coreColz;
        ImageView call;

        public ViewHolder(View itemView) {
            super(itemView);
            colzName = (TextView) itemView.findViewById(R.id.colzName);
            website = (TextView) itemView.findViewById(R.id.address);
            address = (TextView) itemView.findViewById(R.id.webSite);
            desc = (TextView) itemView.findViewById(R.id.desc);
            call = (ImageView) itemView.findViewById(R.id.call);
            coreColz = (FrameLayout) itemView.findViewById(R.id.coreColz);
        }
    }


}