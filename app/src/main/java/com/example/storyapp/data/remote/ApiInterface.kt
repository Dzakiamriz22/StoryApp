package com.example.storyapp.data.remote

import com.example.storyapp.data.model.AddStoryResponse
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.auth.LoginResponse
import com.example.storyapp.data.model.auth.RegisterResponse
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.model.auth.LoginRequest
import com.example.storyapp.data.model.auth.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @POST("register")
    fun registerUser(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 3
    ): Response<StoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Response<StoryResponse>
}
