package com.example.whyCats.model.data

import com.example.whyCats.model.UploadResponse
import com.example.whyCats.model.network.NetworkRepo
import com.example.whyCats.model.network.NetworkService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException


object PostData {
    private val interceptor = NetworkRepo.getInterceptor()
    private val apiClient = NetworkRepo.getClient(interceptor)?.create(NetworkService::class.java)

    suspend fun sendImage(uploadFile: File, onSuccess: () -> Unit) =
        try {
            flow {
                // not sure how or why this works seeing as I am sending .jpg. image/* doesn't work
                val requestFile: RequestBody = uploadFile.asRequestBody("image/png".toMediaTypeOrNull())

                val multipartBody = MultipartBody.Part.createFormData(name="file", filename=uploadFile.path,
                    body=requestFile)

                val response = apiClient?.postCatImage(file = multipartBody)
                response?.let {
                    if (it.approved == 1) {
                        emit(it)
                        onSuccess.invoke()
                    }
                }
            }.single()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        }
}