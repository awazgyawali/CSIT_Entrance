package np.com.aawaz.csitentrance.objects

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Feedback(var message: String) {

    var name: String? = null
    var email: String? = null
    var uuid: String

    init {
        val user = FirebaseAuth.getInstance().currentUser
        this.name = user!!.displayName
        this.email = user.email
        this.uuid = user.uid
    }

}
