package com.example.whyCats.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    val id: String? = null,
    val url: String? = null,
    val width: Int? = 0,
    val height: Int? = 0,
    @SerializedName("original_filename")
    val originalFilename: String? = null,
    val pending: Int? = 0,
    val approved: Int? = 0
)
