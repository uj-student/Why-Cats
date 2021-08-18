package com.example.whyCats.data

import com.example.whyCats.model.data.GetUploadHistory
import com.example.whyCats.model.network.NetworkService
import com.example.whyCats.util.BaseUnitTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class ImageUploadHistoryShould: BaseUnitTest() {

    private val apiClient: NetworkService = mock()

    @Test
    fun getUploadHistory() = coroutinesTestRule.dispatcher.runBlockingTest{
        GetUploadHistory().load(any())

        verify(apiClient, times(1)).getUserUploadHistory()

    }
}