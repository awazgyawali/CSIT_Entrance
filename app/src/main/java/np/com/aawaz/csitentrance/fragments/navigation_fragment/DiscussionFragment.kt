package np.com.aawaz.csitentrance.fragments.navigation_fragment


import android.app.NotificationManager
import android.content.Context
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
import np.com.aawaz.csitentrance.adapters.DiscussionAdapter
import np.com.aawaz.csitentrance.objects.Discussion
import np.com.aawaz.csitentrance.objects.SPHandler

class DiscussionFragment : Fragment(), ChildEventListener {
    val keys: ArrayList<String?> = ArrayList()
    lateinit var adapter: DiscussionAdapter


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
        var index: Int =-1
        keys.forEach {
            index++
            if(it == dataSnapshot.key){
                adapter.editPost(dataSnapshot.getValue(Discussion::class.java),index)
            }
        }
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
        keys.add(0, dataSnapshot.key)
        adapter.addNewPost(dataSnapshot.getValue(Discussion::class.java))
    }

    override fun onChildRemoved(p0: DataSnapshot) {
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        readyRecyclerview()

        FirebaseDatabase.getInstance().reference.child("/discussion/posts").orderByChild("time_stamp").limitToLast(50).addChildEventListener(this)

    }

    private fun readyRecyclerview() {
        adapter= DiscussionAdapter(activity!!)
        discussionRecylerView.layoutManager = LinearLayoutManager(context)
        discussionRecylerView.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discussion, container, false)
    }
}
