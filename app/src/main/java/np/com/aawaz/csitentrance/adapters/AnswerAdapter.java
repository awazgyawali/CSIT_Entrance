package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;


public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    LayoutInflater inflater;
    int size, code;
    Context context;

    //Array list decleration
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> a = new ArrayList<>();
    ArrayList<String> b = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<String> d = new ArrayList<>();
    ArrayList<String> answer = new ArrayList<>();

    public AnswerAdapter(Context context, int size, int code) {
        inflater = LayoutInflater.from(context);
        this.size = size;
        this.code = code;
        this.context = context;
        setDataToArrayList();
    }

    public AnswerAdapter(Context context, int size, int code, int startFrom) {
        inflater = LayoutInflater.from(context);
        this.size = size;
        this.code = code;
        this.context = context;
        setDataToArrayList(startFrom);
    }

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.answer_item_holder, parent, false);
        return new ViewHolder(view);
    }

    public void increaseSize() {
        size++;
        notifyItemInserted(size);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String htm = questions.get(position) + "<br><b>Answer:</b> " + ansFinder(position);
        holder.que.loadDataWithBaseURL("", htm, "text/html", "UTF-8", "");
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json", context));
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

        } catch (Exception ignored) {
        }
    }

    public void setDataToArrayList(int startFrom) {
        //Reading from json file and insillizing inside arrayList
        int endAt = startFrom + 25;
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json", context));
            JSONArray m_jArry = obj.getJSONArray("questions");
            for (int i = startFrom; i < endAt; i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                questions.add(jo_inside.getString("question"));
                a.add(jo_inside.getString("a"));
                b.add(jo_inside.getString("b"));
                c.add(jo_inside.getString("c"));
                d.add(jo_inside.getString("d"));
                answer.add(jo_inside.getString("ans"));
            }
        } catch (Exception ignored) {
        }
    }

    public String ansFinder(int position) {
        if (answer.get(position).equals("a")) {
            return a.get(position);
        } else if (answer.get(position).equals("b")) {
            return b.get(position);
        } else if (answer.get(position).equals("c")) {
            return c.get(position);
        } else if (answer.get(position).equals("d")) {
            return d.get(position);
        } else {
            return "Somthing went wrong";
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        WebView que;

        public ViewHolder(View itemView) {
            super(itemView);
            que = (WebView) itemView.findViewById(R.id.answerHolder);

        }

    }


}

