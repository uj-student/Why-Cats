package com.example.whyCats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whyCats.model.data.PostData
import kotlinx.coroutines.launch
import java.io.File

class ImageUploadViewModel : ViewModel() {

    fun uploadCatImage(catImagePath: String, onUploadSuccess: () -> Unit) {
        viewModelScope.launch {
            PostData.sendImage(File(catImagePath)) {
                onUploadSuccess.invoke()
            }
        }
    }
}