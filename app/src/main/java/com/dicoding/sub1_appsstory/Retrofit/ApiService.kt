package com.dicoding.sub1_appsstory.Retrofit

import com.dicoding.sub1_appsstory.Data.Login
import com.dicoding.sub1_appsstory.Data.Register
import com.dicoding.sub1_appsstory.Data.DetailStoryResp
import com.dicoding.sub1_appsstory.Data.ReceiveResp
import com.dicoding.sub1_appsstory.Data.GetStoryResp
import com.dicoding.sub1_appsstory.Data.LoginResp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body requestBody: Register
    ): ReceiveResp

    @POST("login")
    suspend fun login(
        @Body requestBody: Login
    ): LoginResp


    @Multipart
    @POST("stories")
    suspend fun sendStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody ): ReceiveResp

    @GET("stories")
    suspend fun getStrories(@Header("Authorization") token: String): GetStoryResp

    @GET("stories/{id}")
    suspend fun getStroryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResp

}