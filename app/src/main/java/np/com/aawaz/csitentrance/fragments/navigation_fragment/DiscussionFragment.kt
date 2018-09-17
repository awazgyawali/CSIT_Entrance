package np.com.aawaz.csitentrance.fragments.navigation_fragment


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_discussion.*
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.activities.AskOnDiscussionActivity
import np.com.aawaz.csitentrance.activities.DiscussionActivity
import np.com.aawaz.csitentrance.activities.MainActivity
import np.com.aawaz.csitentrance.adapters.DiscussionAdapter
import np.com.aawaz.csitentrance.misc.FirebasePaths
import np.com.aawaz.csitentrance.objects.Discussion
import np.com.aawaz.csitentrance.objects.SPHandler

class DiscussionFragment : Fragment(), ChildEventListener {
    val keys: ArrayList<String?> = ArrayList()
    lateinit var adapter: DiscussionAdapter
    val ref = FirebaseDatabase.getInstance().reference.child(FirebasePaths.DISCUSSION_POSTS)

    companion object {
        fun newInstance(post_id: String?): Fragment {
            val forum = DiscussionFragment()
            val args = Bundle()
            args.putString("fragment", "discussion")
            args.putString("post_id", post_id)
            forum.arguments = args
            return forum
        }
    }


    override fun onCancelled(p0: DatabaseError) {
        Toast.makeText(context, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show()
    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

    }

    override fun onResume() {
        super.onResume()
        SPHandler.getInstance().clearUnreadDiscussionCount()
        val managerCompat = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        managerCompat.cancel("opened".hashCode())
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
        var index: Int = -1
        keys.forEach {
            index++
            if (it == dataSnapshot.key) {
                adapter.editPost(dataSnapshot.key!!, dataSnapshot.getValue(Discussion::class.java), index)
            }
        }
    }

    private fun handleIntent() {
        val post_id = arguments?.getString("post_id")
        val fragment = arguments?.getString("fragment")
        if (post_id != null && post_id != "new_post" && !MainActivity.openedIntent && fragment != null && fragment == "discussion") {
            val arr = post_id.split("-")
            startActivity(Intent(context, DiscussionActivity::class.java)
                    .putExtra("code", arr[0].toInt())
                    .putExtra("position", arr[1].toInt()))
            MainActivity.openedIntent = true
        }
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
        if (nothingFound != null)
            nothingFound.visibility = View.GONE
        keys.add(0, dataSnapshot.key)
        adapter.addNewPost(dataSnapshot.getValue(Discussion::class.java), dataSnapshot.key!!)
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        val index = keys.indexOf(dataSnapshot.getKey());
        if (index != -1) {
            adapter.deleteItemAt(index)
            keys.removeAt(index)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        readyRecyclerview()

        askOnDiscussion.setOnClickListener {
            startActivity(Intent(context, AskOnDiscussionActivity::class.java))
        }

        ref.orderByChild("time_stamp").limitToLast(50).addChildEventListener(this)
        handleIntent()
    }

    private fun readyRecyclerview() {
        adapter = DiscussionAdapter(activity!!)
        discussionRecylerView.layoutManager = LinearLayoutManager(context)
        discussionRecylerView.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discussion, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        ref.removeEventListener(this)
    }
}
