package com.nirengi.kapnews.android.data

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val username: String,
    val password: String,
)

data class RegisterRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    @SerializedName("stockCodeList") val stockCodeList: List<String> = emptyList(),
    @SerializedName("patternList") val patternList: List<String> = emptyList(),
)

data class FcmTokenRequest(val fcmToken: String)

interface KapApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): String

    @POST("users/register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("users/me/fcm-token")
    suspend fun updateFcmToken(@Body body: FcmTokenRequest): Response<Unit>
}

/** Minimal fields from backend UserDto JSON. */
data class RegisterResponse(val id: Long?)
