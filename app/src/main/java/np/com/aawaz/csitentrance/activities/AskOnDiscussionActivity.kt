package np.com.aawaz.csitentrance.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_ask_on_discussion.*
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.misc.FirebasePaths
import np.com.aawaz.csitentrance.objects.Comment
import np.com.aawaz.csitentrance.objects.Discussion
import java.io.File
import java.util.*

class AskOnDiscussionActivity : AppCompatActivity() {

    private var image: Image? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_on_discussion)

        val toolbar = findViewById<View>(R.id.toolbarAskDiscussion) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Discussion"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }


        attachImage.setOnClickListener { _ ->
            ImagePicker.create(this)
                    .single()
                    .theme(R.style.AppHalfTheme)
                    .toolbarImageTitle("Select an Image")
                    .includeVideo(false)
                    .start()
        }

        submitDiscussion.setOnClickListener {
            if (image == null)
                postToFirebase(null)
            else
                uploadImage()
        }

        removeAttachment.setOnClickListener { _ ->
            removeAttachment.visibility = View.GONE
            choosedImage.text = "Add an Image"
            choosedImage.setOnClickListener {

            }
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


    private fun uploadImage() {

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
            postToFirebase(fileName)
            progressDialog.dismiss()
            image = null
        }


        uploadTask.addOnFailureListener {
            progressDialog.dismiss()
            it.printStackTrace()
            Toast.makeText(this, "Unable to upload image, please try again later.", Toast.LENGTH_SHORT).show()
        }
    }

    fun postToFirebase(image_url: String?) {
        val discussion = Discussion()
        discussion.comment_count = 0
        discussion.question_message = discussionQuestionText.text.toString()
        discussion.time_stamp = System.currentTimeMillis()
        discussion.image_url = image_url
        discussion.paper_code = "100"
        discussion.question_no = "0"

        val post_ref = FirebaseDatabase.getInstance().reference.child(FirebasePaths.DISCUSSION_POSTS).push()

        //adding an comment
        val comment_ref = FirebaseDatabase.getInstance().reference.child(FirebasePaths.DISCUSSION_COMMENTS).child(post_ref.key!!).push()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val comment = Comment(currentUser!!.uid, currentUser.displayName, System.currentTimeMillis(), "Help me on this question.", currentUser.photoUrl!!.toString(), null)
        comment_ref.setValue(comment)

        //setting the question data
        post_ref.setValue(discussion)


        Toast.makeText(this, "Discussion posted.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setupAttachmentView() {
        choosedImage.text = image?.name
        choosedImage.setOnClickListener {
            startActivity(Intent(this, ImageViewingActivity::class.java).putExtra("path", image?.path).putExtra("local", true))
        }
        removeAttachment.visibility = View.VISIBLE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
