package com.example.whyCats.model.data

import com.example.whyCats.model.network.NetworkRepo
import com.example.whyCats.model.network.NetworkService
import kotlinx.coroutines.flow.catch
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

    // need to come back here and clean up
    suspend fun sendImage(uploadFile: File, onSuccess: () -> Unit, onError: () -> Unit) =
        try {
            flow {
                val requestFile: RequestBody =
                    uploadFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                val requestFile: RequestBody = uploadFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())

                val multipartBody = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = uploadFile.path,
                    body = requestFile
                )

                val response = apiClient?.postCatImage(file = multipartBody)
                response?.let {
                    if (it.approved == 1) {
                        emit(it)
                        onSuccess.invoke()
                    } else {
                        onError.invoke()
                    }
                }
            }.catch {
                onError.invoke()
            }.single()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        }
}