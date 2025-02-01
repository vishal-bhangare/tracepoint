package com.example.tracepoint.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracepoint.api.RetrofitClient
import com.example.tracepoint.models.AuthResponse
import com.example.tracepoint.models.LoginRequest
import com.example.tracepoint.models.LoginResponse
import com.example.tracepoint.models.RegisterRequest
import com.example.tracepoint.utils.Resource
import com.example.tracepoint.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authResult = MutableLiveData<Resource<AuthResponse>>()
    val authResult: LiveData<Resource<AuthResponse>> = _authResult

    private val _authLoginResult = MutableLiveData<Resource<LoginResponse>>()
    val authLoginResult: LiveData<Resource<LoginResponse>> = _authLoginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authLoginResult.value = Resource.Loading()
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.status) {
                            loginResponse._id?.let { id ->
                                SharedPrefsManager.saveUserId(id)
                            }
                            SharedPrefsManager.setLoggedIn(true)
                            _authLoginResult.value = Resource.Success(loginResponse)
                        } else {
                            _authLoginResult.value = Resource.Error("Invalid credentials")
                        }
                    }
                } else {
                    _authLoginResult.value = Resource.Error("Login failed")
                }
            } catch (e: Exception) {
                _authLoginResult.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun register(name: String, email: String, contact: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _authResult.value = Resource.Loading()
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(name, email, contact, password, confirmPassword)
                )
                if (response.isSuccessful) {
                    _authResult.value = Resource.Success(response.body()!!)
                } else {
                    _authResult.value = Resource.Error("Registration failed")
                }
            } catch (e: Exception) {
                _authResult.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }
}