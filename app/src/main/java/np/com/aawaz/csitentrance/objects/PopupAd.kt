package np.com.aawaz.csitentrance.objects

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class PopupAd {
    var banner_image: String? = null
    var title: String? = null
    var address: String? = null
    var website: String? = null
    var detail: String? = null
    var phone: String? = null
}
