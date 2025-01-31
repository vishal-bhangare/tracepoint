package com.example.tracepoint.api


import com.example.tracepoint.models.AuthResponse
import com.example.tracepoint.models.CreatePostRequest
import com.example.tracepoint.models.LoginRequest
import com.example.tracepoint.models.LoginResponse
import com.example.tracepoint.models.Post
import com.example.tracepoint.models.RegisterRequest
import com.example.tracepoint.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @POST("api/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/user/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @GET("api/user/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    @GET("api/post/lost")
    suspend fun getLostPosts(): Response<List<Post>>

    @GET("api/post/found")
    suspend fun getFoundPosts(): Response<List<Post>>

    @GET("api/post/found")
    suspend fun getUserFoundPosts(@Query("user") userId: String? = null): Response<List<Post>>

    @GET("api/post/lost")
    suspend fun getUserLostPosts(@Query("user") userId: String? = null): Response<List<Post>>

    @GET("api/post/{postId}")
    suspend fun getPost(@Path("postId") postId: String): Response<Post>

    @POST("api/post")
    suspend fun createPost(@Body createPostRequest: CreatePostRequest): Response<Post>
}
