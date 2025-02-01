package com.example.tracepoint.api


import com.example.tracepoint.models.AuthResponse
import com.example.tracepoint.models.CreatePostRequest
import com.example.tracepoint.models.LoginRequest
import com.example.tracepoint.models.LoginResponse
import com.example.tracepoint.models.Post
import com.example.tracepoint.models.RegisterRequest
import com.example.tracepoint.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

//    @POST("api/post")
//    suspend fun createPost(@Body createPostRequest: CreatePostRequest): Response<Post>

    @Multipart
    @POST("api/post")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("author") author: RequestBody,
        @Part("type") type: RequestBody,
        @Part("location") location: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<Post>
}
