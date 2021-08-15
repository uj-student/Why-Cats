package com.example.whyCats.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    @SerializedName("original_filename")
    val originalFilename: String,
    val pending: Int,
    val approved: Int
)
