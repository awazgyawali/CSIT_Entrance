package np.com.aawaz.csitentrance.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_image_viewing.*
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.misc.GlideApp

class ImageViewingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewing)
        val local = intent.getBooleanExtra("local", true)
        val path = intent.getStringExtra("path")
        if (local)
            GlideApp.with(this).load(path).placeholder(R.drawable.placeholder).into(myImageView)
        else {
            val ref = FirebaseStorage.getInstance().getReference("discussion/$path")
            GlideApp.with(this).load(ref).placeholder(R.drawable.placeholder).into(myImageView)
        }

    }
}
