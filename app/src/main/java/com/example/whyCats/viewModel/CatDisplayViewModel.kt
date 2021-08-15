package com.example.whyCats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.whyCats.model.data.GetData

class CatDisplayViewModel : ViewModel() {

    val cat = Pager(PagingConfig(pageSize = 10)) {
        GetData()
    }.flow.cachedIn(viewModelScope)

}