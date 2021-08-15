package com.example.whyCats.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UploadHistoryResponse(
    val id: String,
    val url: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("original_filename")
    val uploadedFileName: String,
    val width: Int,
    val height: Int,
    val approved: Int,
    val pending: Int
): Serializable