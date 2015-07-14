package np.com.aawaz.csitentrance;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SlideUpPanelAdapter extends RecyclerView.Adapter<SlideUpPanelAdapter.ViewHolder> {
    LayoutInflater inflater;
    int size,code;
    Context context;

    //Array list decleration
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> a = new ArrayList<>();
    ArrayList<String> b = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<String> d = new ArrayList<>();
    ArrayList<String> answer = new ArrayList<>();

    public SlideUpPanelAdapter(Context context,int size,int code){
        inflater=LayoutInflater.from(context);
        this.size=size;
        this.code=code;
        this.context=context;
        setDataToArrayList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.answer_item_holder, parent, false);
        return new ViewHolder(view);
    }

    public void increaseSize(){
        size++;
        notifyItemInserted(size);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.que.setText(Html.fromHtml(questions.get(position)));
        holder.ans.setText(Html.fromHtml(ansFinder(position)));
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json",context));
            JSONArray m_jArry = obj.getJSONArray("questions");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                questions.add(jo_inside.getString("question"));
                a.add(jo_inside.getString("a"));
                b.add(jo_inside.getString("b"));
                c.add(jo_inside.getString("c"));
                d.add(jo_inside.getString("d"));
                answer.add(jo_inside.getString("ans"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ansFinder(int position){
        if(answer.get(position).equals("a")) {
            return a.get(position);
        } else if(answer.get(position).equals("b")) {
            return b.get(position);
        } else if(answer.get(position).equals("c")) {
            return c.get(position);
        } else if(answer.get(position).equals("d")) {
            return d.get(position);
        } else {
            return "SOmthing went wrong";
        }
    }

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView que;
        TextView ans;

        public ViewHolder(View itemView) {
            super(itemView);
            que= (TextView) itemView.findViewById(R.id.ques);
            ans= (TextView) itemView.findViewById(R.id.answ);

        }

    }


}

