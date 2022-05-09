package com.example.vitsi.presentation.ui.upload.select_media

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.vitsi.repo.local.media.DefaultLocalMediaRepo
import com.example.vitsi.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch

class SelectMediaViewModel : BaseViewModel() {

    val localMediaRepo by lazy { DefaultLocalMediaRepo() }

    fun initViewModel(context: Context) {
        viewModelScope.launch {
            localMediaRepo.getAllImages(context)
            localMediaRepo.getAllVideos(context)
        }
    }

}