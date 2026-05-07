package com.nirengi.kapnews.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nirengi.kapnews.android.KapApplication
import com.nirengi.kapnews.android.data.LoginRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onRegister: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as KapApplication
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("KAP News", style = MaterialTheme.typography.headlineMedium)
        Text("Giriş yap", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Kullanıcı adı") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                error = null
                loading = true
                scope.launch {
                    try {
                        val token =
                            app.kapApi.login(LoginRequest(username.trim(), password))
                        app.saveJwt(token.trim())
                        onLoggedIn()
                    } catch (e: HttpException) {
                        error = "Giriş başarısız (${e.code()})"
                    } catch (e: Exception) {
                        error = e.message ?: "Bilinmeyen hata"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading && username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Giriş")
            }
        }

        TextButton(onClick = onRegister, modifier = Modifier.fillMaxWidth()) {
            Text("Hesap oluştur")
        }
    }
}
