package np.com.aawaz.csitentrance.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_discussion.*
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.adapters.CommentAdapter
import np.com.aawaz.csitentrance.interfaces.ClickListener
import np.com.aawaz.csitentrance.misc.FirebasePaths
import np.com.aawaz.csitentrance.misc.GlideApp
import np.com.aawaz.csitentrance.misc.Singleton
import np.com.aawaz.csitentrance.objects.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class DiscussionActivity : AppCompatActivity(), ChildEventListener {


    internal lateinit var adapter: CommentAdapter
    internal lateinit var reference: DatabaseReference
    internal var key: ArrayList<String> = ArrayList()
    private var image: Image? = null
    private lateinit var imm: InputMethodManager


    private fun readyCommentButton() {
        EventSender().logEvent("opened_discussion_post")

        addCommentTextDiscussion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                commentDiscussionButton.isEnabled = charSequence.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        commentDiscussionButton.setOnClickListener {
            when {
                image != null -> uploadImageToFirebase()
                addCommentTextDiscussion.text.toString().isNotEmpty() -> sendPostRequestThroughGraph(addCommentTextDiscussion.text.toString(), null)
                else -> Toast.makeText(this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
        attachImageInComment.setOnClickListener {
            ImagePicker.create(this)
                    .single()
                    .theme(R.style.AppHalfTheme)
                    .toolbarImageTitle("Select an Image")
                    .includeVideo(false)
                    .start()
        }
        removeAttachment.setOnClickListener {
            image = null
            itemSelected.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        reference.removeEventListener(this)
    }

    private fun uploadImageToFirebase() {
        val fileName = random().replace("/", "") + "." + image?.name!!.split(".")[1];
        val ref = FirebaseStorage.getInstance().getReference("discussion").child(fileName)

        val file = Uri.fromFile(File(image?.path))
        val uploadTask = ref.putFile(file)

        val progressDialog = MaterialDialog.Builder(this)
                .content("Uploading....")
                .progress(false, 100)
                .autoDismiss(false)
                .cancelable(false)
                .build()

        progressDialog.show()

        uploadTask.addOnProgressListener {
            val data = it.bytesTransferred / it.totalByteCount * 100;
            val progress = data.toInt()
            progressDialog.setProgress(progress)
        }

        uploadTask.addOnSuccessListener {
            sendPostRequestThroughGraph(addCommentTextDiscussion.text.toString(), fileName)
            progressDialog.dismiss()
            image = null
            itemSelected.visibility = View.GONE
        }


        uploadTask.addOnFailureListener {
            progressDialog.dismiss()
            it.printStackTrace()
            Toast.makeText(this, "Unable to upload image, please try again later.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun random(): String {
        val generator = Random()
        val randomStringBuilder = StringBuilder(50)
        var tempChar: Char
        for (i in 0 until 30) {
            tempChar = (generator.nextInt(96) + 32).toChar()
            randomStringBuilder.append(tempChar)
        }
        return randomStringBuilder.toString()
    }

    private fun sendPostRequestThroughGraph(message: String, imagePath: String?) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        val comment = Comment(currentUser!!.uid, currentUser.displayName, System.currentTimeMillis(), message, currentUser.photoUrl!!.toString(), imagePath)
        val postValues = comment.toMap()

        reference.push().setValue(postValues)
        addCommentTextDiscussion.setText("")
        Toast.makeText(this, "Comment added.", Toast.LENGTH_SHORT).show()

        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)


        commentsRecyDiscussion.scrollToPosition(adapter.itemCount - 1)
    }

    private fun fillViews() {
        commentsRecyDiscussion.layoutManager = LinearLayoutManager(this)
        adapter = CommentAdapter(this, true)
        commentsRecyDiscussion.adapter = adapter
        adapter.setClickEventListener(object : ClickListener {
            override fun itemClicked(view: View, position: Int) {

            }

            override fun itemLongClicked(view: View, position: Int) {
                if (FirebaseAuth.getInstance().currentUser!!.uid == adapter.getUidAt(position) || SPHandler.containsDevUID(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    MaterialDialog.Builder(this@DiscussionActivity)
                            .title("Select any option")
                            .items("Edit", "Delete")
                            .itemsCallback { dialog, itemView, which, text ->
                                if (which == 0)
                                    showDialogToEdit(adapter.getMessageAt(position), position)
                                else if (which == 1) {
                                    reference.child(key[position]).removeValue()
                                }
                            }
                            .build()
                            .show()
                }
            }

            override fun upVoteClicked(view: View, position: Int) {
                val post_id = "${intent.getIntExtra("code", 0)}-${intent.getIntExtra("position", 0)}"
                Singleton.getInstance().upvoteDiscussionComment(post_id, key[position], FirebaseAuth.getInstance().currentUser!!.uid, adapter.getUidAt(position), adapter.haveIVoted(position))
                adapter.upVoteAtPosition(position)
            }
        })

        readyCommentButton()

        addListener()

    }

    private fun showDialogToEdit(message: String, position: Int) {
        val dialog = MaterialDialog.Builder(this)
                .title("Edit post")
                .input("Your message", message, false) { dialog, input ->
                    val map = HashMap<String, Any>()
                    map["message"] = input.toString()
                    reference.child(key[position]).updateChildren(map)
                }
                .positiveText("Save")
                .build()

        dialog.inputEditText!!.setLines(5)
        dialog.inputEditText!!.setSingleLine(false)
        dialog.inputEditText!!.maxLines = 7
        dialog.show()
    }


    private fun addListener() {
        reference.addChildEventListener(this)
    }


    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
        val newPost = dataSnapshot.getValue(Comment::class.java)
        adapter.addToTop(newPost)
        key.add(dataSnapshot.key!!)
        errorDiscussion.visibility = View.GONE
    }

    override fun onChildChanged(snapshot: DataSnapshot, k: String?) {
        for (i in 0..(key.size - 1)) {
            if (snapshot.key == key[i])
                adapter.editItemAtPosition(i, snapshot.getValue(Comment::class.java))
        }

    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        for (i in 0..(key.size - 1)) {
            if (snapshot.key == key[i]) {
                adapter.removeItemAtPosition(i)
                key.removeAt(i)
                break
            }
        }
    }


    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

    override fun onCancelled(databaseError: DatabaseError) {
        errorDiscussion.visibility = View.VISIBLE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        val toolbar = findViewById<View>(R.id.toolbarDiscussion) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Discussion"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }

        val rootReference = FirebaseDatabase.getInstance().reference

        reference = if (intent.getStringExtra("discussion") == null)
            rootReference.child(FirebasePaths.DISCUSSION_COMMENTS).child("${intent.getIntExtra("code", 0)}-${intent.getIntExtra("position", 0)}")
        else {
            rootReference.child(FirebasePaths.DISCUSSION_COMMENTS).child(intent.getStringExtra("key"))
        }



        if (intent.getStringExtra("discussion") == null) {
            val question: Question = Singleton.getInstance().getQuestionAt(this, intent.getIntExtra("code", 0), intent.getIntExtra("position", 0))
            fillPost(question)
        } else {
            val discussion = Gson().fromJson(intent.getStringExtra("discussion"), Discussion::class.java)
            fillPost(discussion.question_message)
            if (discussion.image_url != null) {
                discussionImage.visibility = View.VISIBLE
                val ref = FirebaseStorage.getInstance().reference.child("discussion/" + discussion.image_url)
                GlideApp.with(this).load(ref).placeholder(R.drawable.placeholder).into(discussionImage)
                discussionImage.setOnClickListener { startActivity(Intent(this, ImageViewingActivity::class.java).putExtra("path", discussion.image_url).putExtra("local", false)) }
            }
        }
        fillViews()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            if (image != null) {
                this.image = image

                setupAttachmentView()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupAttachmentView() {
        itemSelected.visibility = View.VISIBLE
        imageName.text = image?.name
        itemSelected.setOnClickListener {
            startActivity(Intent(this, ImageViewingActivity::class.java).putExtra("path", image?.path).putExtra("local", true))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillPost(question: String?) {
        val htmlData = "<html lang=\"en\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:500,700\" rel=\"stylesheet\">" +
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
                "<div>" + question
        "</div>" +
                "</body>" +
                "</html>"

        questionPaper.text = "CSIT Entrance"
        questionViewDiscussion.loadDataWithBaseURL("", htmlData, "text/html", "utf-8", null)

    }

    private fun fillPost(question: Question) {
        var htmlData = "";
        htmlData = "<html lang=\"en\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:500,700\" rel=\"stylesheet\">" +
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
                "<div>" + question.question +
                "<br>" + "a) " + question.a +
                "<br>" + "b) " + question.b +
                "<br>" + "c) " + question.c +
                "<br>" + "d) " + question.d +
                "<br>" + "<b>Answer: " +
                question.ans +
                "</b><br>" +
                "</div>" +
                "</body>" +
                "</html>"
        questionPaper.text = resources.getStringArray(R.array.years)[intent.getIntExtra("code", 0)]
        questionViewDiscussion.loadDataWithBaseURL("", htmlData, "text/html", "utf-8", null)
    }
}
