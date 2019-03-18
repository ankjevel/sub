package sub

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions.Builder
import java.io.FileInputStream

class App {
    init {
        FirebaseApp.initializeApp(
            Builder().apply {
                setCredentials(
                    GoogleCredentials.fromStream(
                        FileInputStream(
                            Config.Firebase.credentials
                        )
                    )
                )
            }.build()
        )

        listener = RedisListener()
    }

    companion object {
        var listener: RedisListener? = null
    }
}


fun main() {
    App()
}
