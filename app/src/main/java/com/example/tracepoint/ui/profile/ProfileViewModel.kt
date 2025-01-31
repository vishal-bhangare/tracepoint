package com.example.tracepoint.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracepoint.api.RetrofitClient
import com.example.tracepoint.models.Post
import com.example.tracepoint.models.User
import com.example.tracepoint.utils.Resource
import com.example.tracepoint.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> = _userProfile

    private val _userLostItems = MutableLiveData<List<Post>>()
    val userLostItems: LiveData<List<Post>> = _userLostItems

    private val _userFoundItems = MutableLiveData<List<Post>>()
    val userFoundItems: LiveData<List<Post>> = _userFoundItems

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadUserProfile() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val userId = SharedPrefsManager.getUserId()
                userId?.let { id ->
                    val response = RetrofitClient.apiService.getUser(id)
                    if (response.isSuccessful) {
                        _userProfile.value = response.body()
                        loadUserPosts(id)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadUserLostItems() {
        viewModelScope.launch {
            try {
                val userId = SharedPrefsManager.getUserId()
                userId?.let { id ->
                    val response = RetrofitClient.apiService.getUserLostPosts(id)
                    if (response.isSuccessful) {
                        _userLostItems.value = response.body()
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadUserFoundItems() {
        viewModelScope.launch {
            try {
                val userId = SharedPrefsManager.getUserId()
                userId?.let { id ->
                    val response = RetrofitClient.apiService.getUserFoundPosts(id)
                    if (response.isSuccessful) {
                        _userFoundItems.value = response.body()
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadUserPosts(userId: String) {
        loadUserLostItems()
        loadUserFoundItems()
    }

    fun logout() {
        SharedPrefsManager.setLoggedIn(false)
        SharedPrefsManager.clearUserData()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
