package com.example.tracepoint.ui.post

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracepoint.api.RetrofitClient
import com.example.tracepoint.models.CreatePostRequest
import com.example.tracepoint.models.Location
import com.example.tracepoint.models.Post
import com.example.tracepoint.models.User
import com.example.tracepoint.utils.Resource
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val _postCreationResult = MutableLiveData<Resource<Post>>()
    val postCreationResult: LiveData<Resource<Post>> = _postCreationResult

    private val _postDetails = MutableLiveData<Post>()
    val postDetails: LiveData<Post> = _postDetails

    private val _authorDetails = MutableLiveData<User>()
    val authorDetails: LiveData<User> = _authorDetails

    fun createPost(title: String, description: String, location: Location, type: Boolean, images: List<Uri>) {
        viewModelScope.launch {
            _postCreationResult.value = Resource.Loading()
            try {
                // TODO: Implement image upload logic
                val imageUrls = uploadImages(images)

                val response = RetrofitClient.apiService.createPost(
                    CreatePostRequest(
                        title = title,
                        description = description,
                        location = location,
                        type = type,
                        author = getCurrentUserId(),
                        images = imageUrls
                    )
                )

                if (response.isSuccessful) {
                    _postCreationResult.value = Resource.Success(response.body()!!)
                } else {
                    _postCreationResult.value = Resource.Error("Failed to create post")
                }
            } catch (e: Exception) {
                _postCreationResult.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun getPostDetails(postId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getPost(postId)
                if (response.isSuccessful) {
                    _postDetails.value = response.body()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getAuthorDetails(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getUser(userId)
                if (response.isSuccessful) {
                    _authorDetails.value = response.body()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private suspend fun uploadImages(images: List<Uri>): List<String> {
        // TODO: Implement image upload logic
        return emptyList()
    }

    private fun getCurrentUserId(): String {
        // TODO: Implement get current user logic
        return ""
    }
}
