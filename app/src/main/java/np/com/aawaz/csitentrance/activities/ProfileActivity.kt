package np.com.aawaz.csitentrance.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.adapters.ForumAdapter
import np.com.aawaz.csitentrance.interfaces.ClickListener
import np.com.aawaz.csitentrance.misc.Singleton
import np.com.aawaz.csitentrance.objects.Post

class ProfileActivity : AppCompatActivity(), ValueEventListener {


    lateinit var uid: String
    lateinit var nameText: TextView
    private lateinit var totalScore: TextView
    lateinit var imageView: ImageView

    lateinit var physics: TextView
    lateinit var chemistry: TextView
    lateinit var math: TextView
    lateinit var english: TextView

    lateinit var forumRecyclerView: RecyclerView
    lateinit var adapter: ForumAdapter

    lateinit var loading: ProgressBar

    lateinit var toolbarProfile: Toolbar

    lateinit var noPost: TextView
    var keys: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        toolbarProfile = findViewById(R.id.toolbarProfile)
        setSupportActionBar(toolbarProfile)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        title = ""



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        uid = intent.getStringExtra("uid")
        nameText = findViewById(R.id.profileName)
        totalScore = findViewById(R.id.profileTotalScore)
        imageView = findViewById(R.id.profileImage)

        physics = findViewById(R.id.physics)
        math = findViewById(R.id.math)
        chemistry = findViewById(R.id.chemistry)
        english = findViewById(R.id.english)

        forumRecyclerView = findViewById(R.id.recyclerMyForum)
        adapter = ForumAdapter(this, false)

        loading = findViewById(R.id.loadingData)

        noPost = findViewById(R.id.noPost)

        getUserInfo(uid)

        getScoresOfUser(uid)

        getForumDataOfUser(uid)

    }

    override fun onCancelled(p0: DatabaseError) {
        Toast.makeText(this, "Unable to fetch forum data", Toast.LENGTH_SHORT).show()
    }

    override fun onDataChange(data: DataSnapshot) {
        adapter.clearPosts()
        keys.clear()
        if (data.childrenCount.toInt() == 0)
            noPost.visibility = View.VISIBLE
        data.children.forEach {
            val newPost = it.getValue(Post::class.java)
            if (newPost?.author != null) {
                keys.add(it.key!!)
                adapter.addToTop(newPost, it.key)
            }
        }
    }

    private fun getForumDataOfUser(uid: String) {
        forumRecyclerView.layoutManager = LinearLayoutManager(this)
        forumRecyclerView.adapter = adapter
        adapter.setClickListener(object : ClickListener {
            override fun itemClicked(view: View?, position: Int) {
                startActivity(Intent(this@ProfileActivity, CommentsActivity::class.java)
                        .putExtra("key", adapter.getKeyAt(position))
                        .putExtra("message", adapter.getMessageAt(position))
                        .putExtra("comment_count", adapter.getCommentCount(position)))
            }

            override fun itemLongClicked(view: View?, position: Int) {
            }

            override fun upVoteClicked(view: View?, position: Int) {
                Singleton.getInstance().upvoteAPost(keys[position], FirebaseAuth.getInstance().currentUser!!.uid, adapter.getUidAt(position), adapter.haveIVoted(position))
                adapter.upVoteAtPosition(position)
            }

        })
        FirebaseDatabase.getInstance().getReference("forum_data/posts").orderByChild("uid").startAt(uid).endAt(uid)
                .addValueEventListener(this)
    }

    private fun getUserInfo(uid: String) {
        FirebaseFirestore.getInstance().collection("users").document(uid).addSnapshotListener { snapshot, _ ->
            loading.visibility = View.GONE
            val data = snapshot?.data

            if (data?.get("likesCount") != null)
                upvotes.text = "Upvotes: ${data["likesCount"].toString()}"
            else
                upvotes.text = "Upvotes: 0"

            nameText.text = data?.get("name").toString()
            totalScore.text = "Total Score: ${data?.get("score").toString()}"
            Picasso.with(this)
                    .load(data?.get("image_url").toString())
                    .into(imageView)
        }
    }

    private fun getScoresOfUser(uid: String) {
        FirebaseFirestore.getInstance().collection("scores").document(uid).addSnapshotListener { snapshot, _ ->
            loading.visibility = View.GONE
            val data = snapshot?.data
            physics.text = "${data?.get("physics").toString()}\nPhysics"
            chemistry.text = "${data?.get("chemistry").toString()}\nChemistry"
            english.text = "${data?.get("english").toString()}\nEnglish"
            math.text = "${data?.get("math").toString()}\nMathematics"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
