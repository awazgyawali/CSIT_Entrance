package np.com.aawaz.csitentrance.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_ask_on_discussion.*
import np.com.aawaz.csitentrance.R

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

        }

        removeAttachment.setOnClickListener {_->
            removeAttachment.visibility= View.GONE
            choosedImage.text = "Add an Image"
            choosedImage.setOnClickListener {

            }
        }


    }

    private fun setupAttachmentView() {
        choosedImage.text = image?.name
        choosedImage.setOnClickListener {
            startActivity(Intent(this, ImageViewingActivity::class.java).putExtra("path", image?.path).putExtra("local", true))
        }
        removeAttachment.visibility= View.VISIBLE
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

}
