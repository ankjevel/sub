package sub

import com.google.firebase.messaging.*

object PushNotification {
    private val context = FirebaseMessaging.getInstance()

    fun sendNotification(notification: HashMap<String, String>) {
        println("send notification \"${Config.Firebase.topic}\"")
        context.send(
            Message.builder().apply {
                putAllData(notification)
                setTopic(Config.Firebase.topic)
            }.build()
        )
    }
}