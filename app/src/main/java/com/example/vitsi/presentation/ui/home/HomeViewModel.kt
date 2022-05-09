package com.example.vitsi.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitsi.models.succeeded
import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.repo.network.user.DefaultUserRepo
import com.example.vitsi.repo.network.user.UserRepo
import com.example.vitsi.repo.network.videos.DefaultVideosRepo
import com.example.vitsi.repo.network.videos.VideosRepo
import com.example.vitsi.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel (
    val userRepo: UserRepo = DefaultUserRepo(),
    val videosRepo: VideosRepo = DefaultVideosRepo()
): BaseViewModel() {

    private var _listOfRemoteVideo = MutableLiveData<List<RemoteVideo>>()
    val listOfRemoteVideo: LiveData<List<RemoteVideo>> = _listOfRemoteVideo

    private var isFetching = false

    init {
        fetchVideos()
    }

    fun fetchVideos() {
        if (!isFetching) {
            isFetching = true
            viewModelScope.launch {
                val result = videosRepo.fetchRandomAudios()
                if (result.succeeded)
                    _listOfRemoteVideo.value = result.tryData() ?: listOf()
                isFetching = false
            }
        }
    }

}