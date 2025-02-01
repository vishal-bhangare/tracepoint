package com.example.tracepoint.ui.post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
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
import com.example.tracepoint.utils.SharedPrefsManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val _postCreationResult = MutableLiveData<Resource<Post>>()
    val postCreationResult: LiveData<Resource<Post>> = _postCreationResult

    private val _postDetails = MutableLiveData<Post>()
    val postDetails: LiveData<Post> = _postDetails

    private val _authorDetails = MutableLiveData<User>()
    val authorDetails: LiveData<User> = _authorDetails

    fun createPost(title: String, description: String, author: String,location: Location, type: Boolean, images: List<Uri>) {
        viewModelScope.launch {
            _postCreationResult.value = Resource.Loading()

            try {
                // Convert text fields to RequestBody
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val authorBody = author.toRequestBody("text/plain".toMediaTypeOrNull())
                val typeBody = type.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val locationBody = Gson().toJson(location).toRequestBody("application/json".toMediaTypeOrNull())

                // Convert images to MultipartBody.Parts
                val imageParts = images.map { uri ->
                    val stream = context.contentResolver.openInputStream(uri)
                    val request = stream.use {
                        it?.readBytes()?.toRequestBody("image/*".toMediaTypeOrNull())
                    }
                    MultipartBody.Part.createFormData(
                        "images",
                        "image_${System.currentTimeMillis()}.jpg",
                        request!!
                    )
                }

                val response = RetrofitClient.apiService.createPost(
                    titleBody,
                    descriptionBody,
                    authorBody,
                    typeBody,
                    locationBody,
                    imageParts
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


}
