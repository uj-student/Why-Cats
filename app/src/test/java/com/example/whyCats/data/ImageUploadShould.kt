package com.example.whyCats.data

import com.example.whyCats.model.data.PostData
import com.example.whyCats.model.network.NetworkService
import com.example.whyCats.util.BaseUnitTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MultipartBody
import org.junit.Test
import java.io.File


@ExperimentalCoroutinesApi
class ImageUploadShould : BaseUnitTest() {

    private val apiClient: NetworkService = mock()
    private val onSuccess: Unit = mock()
    private val onFailure: Unit = mock()
    private val mockMultipartBody: MultipartBody.Part = mock()
    private val mockFile: File = mock()

    @Test
    fun sendImageTest() = coroutinesTestRule.dispatcher.runBlockingTest {
        PostData.sendImage(mockFile, { onSuccess }, { onFailure })

        verify(apiClient, times(1)).postCatImage("", mockMultipartBody)
    }
}