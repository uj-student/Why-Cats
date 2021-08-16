package com.example.whyCats.data

//import com.example.whyCats.util.BaseUnitTest
import androidx.paging.PagingSource
import com.example.whyCats.model.data.GetData
import com.example.whyCats.model.data.GetUploadHistory
import com.example.whyCats.model.network.NetworkRepo
import com.example.whyCats.util.BaseUnitTest
import com.example.whyCats.viewModel.CatDisplayViewModel
import com.example.whyCats.viewModel.UserUploadHistoryViewModel
import com.nhaarman.mockitokotlin2.any
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify

@ExperimentalCoroutinesApi
class ViewModelShould: BaseUnitTest() {

    private val catDataSource: GetData = mock()
    private val imageUploadHistory: GetUploadHistory = mock()

    @Test
    fun catDisplayViewModelTest() = runBlockingTest {
        val viewModel = mockCatData()

        viewModel.cat.first()

//        verify(dataSource, times(1)).registerInvalidatedCallback(any())
        verify(catDataSource, times(1)).invalid
    }

    @Test
    fun userUploadViewModelTest() = runBlockingTest {
        val viewModel = mockUserUploadHistoryViewModel()

        viewModel.uploadHistory.first()

        verify(imageUploadHistory, times(1)).load(any())
    }

    private fun mockCatData(): CatDisplayViewModel {
        return CatDisplayViewModel()
    }

    private fun mockUserUploadHistoryViewModel(): UserUploadHistoryViewModel {
        return UserUploadHistoryViewModel()
    }

}