package com.example.tracepoint.ui.postDetails
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracepoint.api.RetrofitClient
import com.example.tracepoint.models.Post
import com.example.tracepoint.models.User
import com.example.tracepoint.utils.Resource
import kotlinx.coroutines.launch

class PostDetailViewModel : ViewModel() {
    private val _postDetails = MutableLiveData<Resource<Post>>()
    val postDetails: LiveData<Resource<Post>> = _postDetails

    private val _userDetails = MutableLiveData<Resource<User>>()
    val userDetails: LiveData<Resource<User>> = _userDetails

    fun loadUserDetails(userId: String) {
        viewModelScope.launch {
            _userDetails.value = Resource.Loading()
            try {
                val response = RetrofitClient.apiService.getUser(userId)
                if (response.isSuccessful) {
                    _userDetails.value = Resource.Success(response.body()!!)
                } else {
                    _userDetails.value = Resource.Error("Failed to load user details")
                }
            } catch (e: Exception) {
                _userDetails.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun loadPostDetails(postId: String) {
        viewModelScope.launch {
            _postDetails.value = Resource.Loading()
            try {
                val response = RetrofitClient.apiService.getPost(postId)
               if (response.isSuccessful) {
                    response.body()?.let { post ->
                        _postDetails.value = Resource.Success(post)
                    }
                } else {
                    _postDetails.value = Resource.Error("Failed to load post details")
                }
            } catch (e: Exception) {
                _postDetails.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }
}
