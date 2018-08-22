package np.com.aawaz.csitentrance.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.DiscussionActivity;
import np.com.aawaz.csitentrance.objects.Question;


public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private int size, code;
    Context context;

    //Array list decleration
    private ArrayList<Question> questions = new ArrayList<>();

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

    private static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.answer_item_holder, parent, false);
        return new ViewHolder(view);
    }

    public void increaseSize() {
        size++;
        notifyItemInserted(size);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String htm = "<html lang=\"en\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:400,700\" rel=\"stylesheet\">" +
                "  <title>Document</title>" +
                "  <style>" +
                "    body{" +
                "      margin: 0;" +
                "    }" +
                "    div {" +
                "      font-size: 18px;" +
                "      line-height: 1.5em;" +
                "      text-align: justify;" +
                "      font-family: 'Work Sans', sans-serif" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "<div>";

        htm = htm + questions.get(position).getQuestion() + "<br><b>Answer:</b> " + ansFinder(position);

        htm = htm + "</div>" +
                "</body>" +
                "</html>";

        holder.que.loadDataWithBaseURL("", htm, "text/html", "UTF-8", "");

        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context).
                        title("Report an issue")
                        .items("Question is mistake", "Options doesn't have the answer", "Unable to view the question.")
                        .negativeText("Cancel")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                String key = context.getResources().getStringArray(R.array.years)[code - 1] + "-" + (holder.getAdapterPosition() + 1);

                                HashMap<String, String> values = new HashMap<>();
                                values.put("issue", text.toString());
                                values.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                values.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                                FirebaseDatabase.getInstance().getReference().child("error_reports").child(key).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(values);
                                Toast.makeText(context, "Thanks for the report", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

        holder.core.setOnTouchListener(new View.OnTouchListener() {
            private final static long MAX_TOUCH_DURATION = 100;
            private long m_DownTime;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        context.startActivity(new Intent(context, DiscussionActivity.class)
                                .putExtra("code", code)
                                .putExtra("position", holder.getAdapterPosition())
                        );
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return size;
    }

    private void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json", context));
            JSONArray m_jArry = obj.getJSONArray("questions");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Question q = new Question(jo_inside.getString("question"), jo_inside.getString("a"), jo_inside.getString("b"), jo_inside.getString("c"), jo_inside.getString("d"), jo_inside.getString("ans"));
                questions.add(q);
            }

        } catch (Exception ignored) {
        }
    }

    private void setDataToArrayList(int startFrom) {
        //Reading from json file and insillizing inside arrayList
        int endAt = startFrom + 25;
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json", context));
            JSONArray m_jArry = obj.getJSONArray("questions");
            for (int i = startFrom; i < endAt; i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Question q = new Question(jo_inside.getString("question"), jo_inside.getString("a"), jo_inside.getString("b"), jo_inside.getString("c"), jo_inside.getString("d"), jo_inside.getString("ans"));
                questions.add(q);
            }
        } catch (Exception ignored) {
        }
    }

    private String ansFinder(int position) {
        Question que = questions.get(position);
        switch (que.getAns()) {
            case "a":
                return que.getA();
            case "b":
                return que.getB();
            case "c":
                return que.getC();
            case "d":
                return que.getD();
            default:
                return "Somthing went wrong";
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        WebView que;
        TextView report;
        View core;

        ViewHolder(View itemView) {
            super(itemView);
            que = itemView.findViewById(R.id.answerHolder);
            report = itemView.findViewById(R.id.drawerReport);
            core = itemView;
        }

    }


}

