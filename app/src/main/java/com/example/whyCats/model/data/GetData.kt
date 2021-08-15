package com.example.whyCats.model.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.whyCats.model.Cat
import com.example.whyCats.model.network.NetworkRepo
import com.example.whyCats.model.network.NetworkService
import retrofit2.HttpException
import java.io.IOException

class GetData : PagingSource<Int, Cat>() {
    private val interceptor = NetworkRepo.getInterceptor()
    private val apiClient = NetworkRepo.getClient(interceptor)?.create(NetworkService::class.java)

    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = apiClient?.getAllCatImages(nextPageNumber)
            // filter out the cats with no details, description etc
            val filteredList = response?.filter { predicate ->
                predicate.breeds!!.isNotEmpty()
            }

            //not using a db so don't want to request too many cats in memory so limit to the first 10 pages
            val nextKey =
                if (response?.isNotEmpty() == true && nextPageNumber < 5) nextPageNumber + 1 else null

            LoadResult.Page(
                data = filteredList ?: listOf(),
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}