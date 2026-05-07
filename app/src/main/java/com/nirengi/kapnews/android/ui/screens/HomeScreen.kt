package com.nirengi.kapnews.android.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging
import com.nirengi.kapnews.android.KapApplication
import com.nirengi.kapnews.android.data.FcmTokenRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val app = LocalContext.current.applicationContext as KapApplication
    val scope = rememberCoroutineScope()
    var pushEnabled by remember { mutableStateOf(true) }
    var tokenStatus by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { pushEnabled = app.readPushEnabled() }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            val res = app.kapApi.updateFcmToken(FcmTokenRequest(token))
            res.errorBody()?.close()
            res.body()?.close()
            tokenStatus =
                if (res.isSuccessful) "Push token sunucuya gönderildi."
                else "Token API hatası (${res.code()})"
        } catch (e: Exception) {
            tokenStatus = "Token gönderilemedi: ${e.message}"
        }
    }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("KAP News", style = MaterialTheme.typography.headlineSmall)
        Text(
            "API: ${com.nirengi.kapnews.android.BuildConfig.API_BASE_URL}",
            style = MaterialTheme.typography.bodySmall,
        )
        Text(tokenStatus, style = MaterialTheme.typography.bodySmall)

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Bildirimler (cihazda)", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("KAP bildirimlerini göster")
                Switch(
                    checked = pushEnabled,
                    onCheckedChange = { v ->
                        scope.launch {
                            app.savePushEnabled(v)
                            pushEnabled = v
                        }
                    },
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    app.saveJwt(null)
                    onLogout()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Çıkış")
        }
    }
}
