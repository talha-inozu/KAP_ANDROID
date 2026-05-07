package com.nirengi.kapnews.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.nirengi.kapnews.android.data.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onCancel: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as KapApplication
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Kayıt", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Kullanıcı adı") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Ad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Soyad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
        if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)

        Button(
            onClick = {
                error = null
                loading = true
                scope.launch {
                    try {
                        val res =
                            app.kapApi.register(
                                RegisterRequest(
                                    username = username.trim(),
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    email = email.trim(),
                                    password = password,
                                ),
                            )
                        if (res.isSuccessful) onRegistered()
                        else error = "Kayıt başarısız (${res.code()})"
                    } catch (e: Exception) {
                        error = e.message ?: "Hata"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled =
                !loading &&
                    username.isNotBlank() &&
                    firstName.isNotBlank() &&
                    lastName.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (loading) CircularProgressIndicator() else Text("Kayıt ol")
        }
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("İptal") }
    }
}
