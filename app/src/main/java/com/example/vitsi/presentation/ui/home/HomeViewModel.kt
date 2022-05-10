package com.example.vitsi.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitsi.domain.succeeded
import com.example.vitsi.domain.audio.RemoteAudio
import com.example.vitsi.data.network.user.DefaultUserRepo
import com.example.vitsi.data.network.user.UserRepo
import com.example.vitsi.data.network.videos.DefaultVideosRepo
import com.example.vitsi.data.network.videos.VideosRepo
import com.example.vitsi.presentation.architecture.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel (
    val userRepo: UserRepo = DefaultUserRepo(),
    val videosRepo: VideosRepo = DefaultVideosRepo()
): BaseViewModel() {

    private var _listOfRemoteAudio = MutableLiveData<List<RemoteAudio>>()
    val listOfRemoteAudio: LiveData<List<RemoteAudio>> = _listOfRemoteAudio

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
                    _listOfRemoteAudio.value = result.tryData() ?: listOf()
                isFetching = false
            }
        }
    }

}