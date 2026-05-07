package com.nirengi.kapnews.android.messaging

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nirengi.kapnews.android.KapApplication
import com.nirengi.kapnews.android.MainActivity
import com.nirengi.kapnews.android.R
import com.nirengi.kapnews.android.data.FcmTokenRequest
import com.nirengi.kapnews.android.data.KapPrefsKeys
import com.nirengi.kapnews.android.data.kapDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class KapFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val show =
            runBlocking {
                kapDataStore.data.map { it[KapPrefsKeys.pushEnabled] ?: true }.first()
            }
        if (!show) return

        val title = message.data["title"] ?: getString(R.string.app_name)
        val body = message.data["body"] ?: return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat.Builder(this, KapApplication.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pending)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(this).notify(message.messageId?.hashCode() ?: 0, notification)
    }

    override fun onNewToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val jwt = KapApplication.instance.readJwt()
            if (!jwt.isNullOrBlank()) {
                try {
                    val res =
                        KapApplication.instance.kapApi.updateFcmToken(FcmTokenRequest(token))
                    res.errorBody()?.close()
                } catch (_: Exception) {
                }
            }
        }
    }
}
