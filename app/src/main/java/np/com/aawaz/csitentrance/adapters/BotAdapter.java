package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.Message;

public class BotAdapter extends RecyclerView.Adapter<BotAdapter.VH> {

    ArrayList<Message> messages = new ArrayList<>();
    private Context context;

    public BotAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).messageType;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new VH(LayoutInflater.from(context).inflate(R.layout.user_message, parent, false));
        else
            return new VH(LayoutInflater.from(context).inflate(R.layout.bot_message, parent, false));

    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.message.setText(messages.get(position).text);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView message;

        public VH(View itemView) {
            super(itemView);
            message =  itemView.findViewById(R.id.message_text);
        }
    }
}
