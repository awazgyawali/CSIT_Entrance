package np.com.aawaz.csitentrance.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.activities.DiscussionActivity
import np.com.aawaz.csitentrance.misc.Singleton
import np.com.aawaz.csitentrance.objects.Discussion

class DiscussionAdapter(private var context: Context) : RecyclerView.Adapter<DiscussionAdapter.VH>() {

    private var inflater = LayoutInflater.from(context)
    private var discussions = ArrayList<Discussion>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = inflater.inflate(R.layout.discussion_item_holder, parent, false)
        return VH(view)
    }

    override fun getItemCount() = discussions.size

    fun addNewPost(discussion: Discussion?) {
        discussions.add(0, discussion!!)
        notifyItemInserted(0)
    }

    fun editPost(discussion: Discussion?, position: Int) {
        discussions.removeAt(position)
        discussions.add(position, discussion!!)
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val discussion = discussions[position]
        val question = Singleton.getInstance().getQuestionAt(context, discussion.paper_code.toInt(), discussion.question_no.toInt())
        var htm = "<html lang=\"en\">" +
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
                "<div>"

        htm = "$htm<p onclick=\"ok.performClick(${discussion.question_no.toInt()});\">"
        htm = htm + question.question + "<br><b>Answer:</b> " + question.ans
        htm = "$htm</p>"

        htm = htm + "</div>" +
                "</body>" +
                "</html>"

        holder.que.loadDataWithBaseURL("", htm, "text/html", "UTF-8", "")
        holder.commentCount.text = (discussion.comment_count.toString() + " comments")

        val ws = holder.que.settings
        ws.javaScriptEnabled = true
        holder.que.addJavascriptInterface(object : Any() {

            @JavascriptInterface
            fun performClick(pos: Int) {

            }
        }, "ok")

        holder.questionPaper.text = context.resources.getStringArray(R.array.years)[discussion.paper_code.toInt()]

        holder.questionPaper.setOnClickListener {
            context.startActivity(Intent(context, DiscussionActivity::class.java)
                    .putExtra("code", discussion.paper_code.toInt())
                    .putExtra("position", discussion.question_no.toInt()))
        }

        holder.commentCount.setOnClickListener {
            context.startActivity(Intent(context, DiscussionActivity::class.java)
                    .putExtra("code", discussion.paper_code.toInt())
                    .putExtra("position", discussion.question_no.toInt()))
        }
    }

    class VH(var core: View) : RecyclerView.ViewHolder(core) {
        val que: WebView = itemView.findViewById(R.id.answerHolder)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val questionPaper: TextView = itemView.findViewById(R.id.questionPaper)
    }
}