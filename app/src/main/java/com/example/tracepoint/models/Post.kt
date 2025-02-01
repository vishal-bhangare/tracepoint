package com.example.tracepoint.models

data class Post(
    val _id: String,
    val title: String,
    val description: String,
    val location: Location,
    val type: Boolean, // true for found, false for lost
    val author: String,
    val images: List<String>
)

data class Location(
    val type: String,
    val coordinates: List<Double>
)

data class CreatePostRequest(
    val title: String,
    val description: String,
    val location: Location,
    val type: Boolean,
    val author: String?,
    val images: List<String>
)
