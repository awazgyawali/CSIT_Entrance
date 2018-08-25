package np.com.aawaz.csitentrance.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
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
import np.com.aawaz.csitentrance.objects.Question


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

    private fun ansFinder(que: Question): String {
        return when (que.ans) {
            "a" -> que.a
            "b" -> que.b
            "c" -> que.c
            "d" -> que.d
            else -> "Somthing went wrong"
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val discussion = discussions[position]
        val question = Singleton.getInstance().getQuestionAt(context, discussion.paper_code.toInt(), discussion.question_no.toInt())
        val answer = ansFinder(question);

        if (question.question.contains("<img") || answer.contains("<img"))
            setupFromWebView(discussion, question, answer, holder)
        else
            setupFromTextView(discussion, question, answer, holder)

        holder.commentCount.text = (discussion.comment_count.toString() + " comments")

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

    private fun setupFromTextView(discussion: Discussion, question: Question?, answer: String, holder: VH) {
        holder.questionTextView.text = fromHtml(question?.question + "<br>" + "<b>Answer:</b> " + answer)
        holder.questionTextView.visibility = View.VISIBLE
        holder.que.visibility = View.GONE
        holder.questionTextView.setOnClickListener {
            context.startActivity(Intent(context, DiscussionActivity::class.java)
                    .putExtra("code", discussion.paper_code.toInt())
                    .putExtra("position", discussion.question_no.toInt()))
        }

    }

    @SuppressWarnings("deprecation")
    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    private fun setupFromWebView(discussion: Discussion, question: Question, answer: String, holder: VH) {
        holder.questionTextView.visibility = View.GONE
        holder.que.visibility = View.VISIBLE
        var htm = "<html lang=\"en\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Work+Sans\" rel=\"stylesheet\">" +
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
        htm = htm + question.question + "<br><b>Answer:</b> " + answer
        htm = "$htm</p>"

        htm = htm + "</div>" +
                "</body>" +
                "</html>"

        holder.que.loadDataWithBaseURL("", htm, "text/html", "UTF-8", "")

        val ws = holder.que.settings
        ws.javaScriptEnabled = true
        holder.que.addJavascriptInterface(object : Any() {

            @JavascriptInterface
            fun performClick(pos: Int) {
                context.startActivity(Intent(context, DiscussionActivity::class.java)
                        .putExtra("code", discussion.paper_code.toInt())
                        .putExtra("position", discussion.question_no.toInt()))
            }
        }, "ok")
    }

    fun deleteItemAt(index: Int) {
        discussions.removeAt(index)
        notifyItemRemoved(index)
    }

    class VH(var core: View) : RecyclerView.ViewHolder(core) {
        val que: WebView = itemView.findViewById(R.id.answerHolder)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val questionPaper: TextView = itemView.findViewById(R.id.questionPaper)
        val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
    }
}