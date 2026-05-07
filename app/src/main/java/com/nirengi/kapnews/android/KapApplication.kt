package com.nirengi.kapnews.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.datastore.preferences.core.edit
import com.nirengi.kapnews.android.data.KapApi
import com.nirengi.kapnews.android.data.KapPrefsKeys
import com.nirengi.kapnews.android.data.createKapRetrofit
import com.nirengi.kapnews.android.data.kapDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

class KapApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val jwtRef = AtomicReference<String?>(null)

    lateinit var kapApi: KapApi
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        kapApi = createKapRetrofit(BuildConfig.API_BASE_URL) { jwtRef.get() }
        createNotificationChannel()
        appScope.launch { jwtRef.set(readJwt()) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch =
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.channel_kap),
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
    }

    suspend fun readJwt(): String? =
        kapDataStore.data.map { it[KapPrefsKeys.jwt] }.first()

    suspend fun saveJwt(token: String?) {
        jwtRef.set(token?.takeIf { it.isNotBlank() })
        kapDataStore.edit { prefs ->
            if (token.isNullOrBlank()) prefs.remove(KapPrefsKeys.jwt) else prefs[KapPrefsKeys.jwt] = token
        }
    }

    suspend fun readPushEnabled(): Boolean =
        kapDataStore.data.map { prefs -> prefs[KapPrefsKeys.pushEnabled] ?: true }.first()

    suspend fun savePushEnabled(enabled: Boolean) {
        kapDataStore.edit { it[KapPrefsKeys.pushEnabled] = enabled }
    }

    companion object {
        const val CHANNEL_ID = "kap_news_channel"
        lateinit var instance: KapApplication
            private set
    }
}
