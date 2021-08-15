package com.example.whyCats.model.network

import com.example.whyCats.BuildConfig
import com.example.whyCats.model.UploadHistoryResponse
import com.example.whyCats.model.Cat
import com.example.whyCats.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.*


interface NetworkService {
    @GET("images/search")
    suspend fun getAllCatImages(
        @Query("page") page: Int? = 1,
        @Query("has_breeds") value: Int? = 1,
        @Query("limit") size: Int? = 10,
        @Query("order") order: String? = "desc",
        @Query("api_key") apiKey: String? = BuildConfig.API_KEY
    ): List<Cat>

    @Multipart
    @POST("images/upload")
    suspend fun postCatImage(
        @Header("x-api-key") apiKey: String? = BuildConfig.API_KEY,
        @Part file: MultipartBody.Part?
    ): UploadResponse

    @GET("images/")
    suspend fun getUserUploadHistory(
        @Query("limit") limit: Int? = 10,
        @Query("page") page: Int? = 1,
        @Query("order") order: String? = "DESC",
        @Header("x-api-key") apiKey: String? = BuildConfig.API_KEY,
    ): List<UploadHistoryResponse>
}