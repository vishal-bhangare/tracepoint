package com.example.tracepoint.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracepoint.api.RetrofitClient
import com.example.tracepoint.models.Post
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _lostItems = MutableLiveData<List<Post>>()
    val lostItems: LiveData<List<Post>> = _lostItems

    private val _foundItems = MutableLiveData<List<Post>>()
    val foundItems: LiveData<List<Post>> = _foundItems

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                val lostResponse = RetrofitClient.apiService.getLostPosts()
                if (lostResponse.isSuccessful) {
                    _lostItems.value = lostResponse.body()
                }

                val foundResponse = RetrofitClient.apiService.getFoundPosts()
                if (foundResponse.isSuccessful) {
                    _foundItems.value = foundResponse.body()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
