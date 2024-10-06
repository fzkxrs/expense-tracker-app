package com.masters.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Body

// 1. Define your data classes
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class RegisterRequest(val username: String, val password: String)
data class RegisterResponse(val success: Boolean, val message: String)

// 2. Create a Retrofit interface for your API
interface LoginService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}

// 3. Create a ViewModel to handle the login logic
class LoginViewModel : ViewModel() {
    private val loginService: LoginService
    private lateinit var sharedPreferences: SharedPreferences

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        loginService = retrofit.create(LoginService::class.java)
    }

    fun initializeSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    fun login(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = loginService.login(LoginRequest(username, password))
                val token = response.token

                // Store the token securely in SharedPreferences
                sharedPreferences.edit().putString("auth_token", token).apply()

                // Trigger success callback
                onSuccess()
            } catch (e: Exception) {
                // Handle login error and trigger error callback
                onError(e.message ?: "Unknown error")
                Log.e("LoginViewModel", "Login error: ${e.message}")
            }
        }
    }

    fun register(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = loginService.register(RegisterRequest(username, password))

                if (response.success) {
                    onSuccess() // Trigger success callback
                } else {
                    onError(response.message) // Trigger error callback with server message
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error") // Handle error
                Log.e("LoginViewModel", "Registration error: ${e.message}")
            }
        }
    }
}