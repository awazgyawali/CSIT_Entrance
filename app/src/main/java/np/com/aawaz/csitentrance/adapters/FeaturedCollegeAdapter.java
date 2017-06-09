package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.robototextview.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.FeaturedCollege;

public class FeaturedCollegeAdapter extends RecyclerView.Adapter<FeaturedCollegeAdapter.VH> {


    private Context context;
    ArrayList<FeaturedCollege> colleges = new ArrayList<>();

    public FeaturedCollegeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.each_featured_college, parent, false));
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        Picasso.with(context)
                .load(colleges.get(holder.getAdapterPosition()).banner_image)
                .into(holder.banner);

        Picasso.with(context)
                .load(colleges.get(holder.getAdapterPosition()).profile_image)
                .into(holder.profilePic);
        holder.name.setText(colleges.get(holder.getAdapterPosition()).name);
        holder.address.setText(colleges.get(holder.getAdapterPosition()).address);
        holder.detail.setText(colleges.get(holder.getAdapterPosition()).detail);

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Call Confirmation")
                        .content("Call " + colleges.get(holder.getAdapterPosition()).phone + "?")
                        .positiveText("Call")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(colleges.get(holder.getAdapterPosition()).phone), null)));
                            }
                        })
                        .show();
            }
        });

        holder.website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Confirmation.")
                        .content("Open " + colleges.get(holder.getAdapterPosition()).website + "?")
                        .positiveText("Open")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(colleges.get(holder.getAdapterPosition()).website)));
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

    public void addToTop(FeaturedCollege college) {
        colleges.add(0, college);
        notifyItemInserted(0);
    }

    public class VH extends RecyclerView.ViewHolder {
        ImageView banner;
        CircleImageView profilePic;
        RobotoTextView name, address, detail;
        TextView call, website;

        public VH(View itemView) {
            super(itemView);
            banner = (ImageView) itemView.findViewById(R.id.imageViewFeatured);
            profilePic = (CircleImageView) itemView.findViewById(R.id.imageViewCollegeProfile);
            name = (RobotoTextView) itemView.findViewById(R.id.titleFeatured);
            address = (RobotoTextView) itemView.findViewById(R.id.addressFeatured);
            detail = (RobotoTextView) itemView.findViewById(R.id.detailFeatured);
            call = (TextView) itemView.findViewById(R.id.callFeatured);
            website = (TextView) itemView.findViewById(R.id.websiteFeatured);
        }
    }
}
