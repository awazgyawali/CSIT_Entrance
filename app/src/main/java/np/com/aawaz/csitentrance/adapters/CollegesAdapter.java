package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;


public class CollegesAdapter extends RecyclerView.Adapter<CollegesAdapter.ViewHolder> {
    ArrayList<String> desc;
    ArrayList<String> website;
    ArrayList<String> phNo;
    ArrayList<String> address;
    Context context;
    LayoutInflater inflater;
    ArrayList<String> colleges;

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
        holder.colzName.setText(colleges.get(position));
        holder.address.setText(address.get(position));
        if (website.get(position).equals("null"))
            holder.website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(MainActivity.mainLayout, "No web address found.", Snackbar.LENGTH_SHORT).show();
                }
            });
        else
            holder.website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(context)
                            .title("Confirmation.")
                            .content("Open " + website.get(position) + "?")
                            .positiveText("Open")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website.get(position))));
                                }
                            })
                            .show();
                }
            });

        if (phNo.get(position).equals("null"))
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(MainActivity.mainLayout, "No phone number found.", Snackbar.LENGTH_SHORT).show();
                }
            });
        else
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(context)
                            .title("Call Confirmation")
                            .content("Call " + phNo.get(position) + "?")
                            .positiveText("Call")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phNo.get(position), null)));
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
        LinearLayout website, call;
        TextView address;
        LinearLayout coreColz;

        public ViewHolder(View itemView) {
            super(itemView);
            colzName = (TextView) itemView.findViewById(R.id.colzName);
            website = (LinearLayout) itemView.findViewById(R.id.webSiteButton);
            address = (TextView) itemView.findViewById(R.id.address);
            call = (LinearLayout) itemView.findViewById(R.id.callButton);
            coreColz = (LinearLayout) itemView.findViewById(R.id.coreColz);
        }
    }


}