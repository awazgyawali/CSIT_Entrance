package np.com.aawaz.csitentrance.objects

//user = 0, bot = 1
class Message(var text: String, var messageType: Int) {
    companion object {
        var BOT = 1
        var USER = 0
    }
}
