package com.example.vitsi.presentation.ui.profile.with_account.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.models.video.VideoType
import com.example.vitsi.repo.network.user.DefaultUserRepo
import com.example.vitsi.repo.network.user.UserRepo
import com.example.vitsi.repo.network.videos.DefaultVideosRepo
import com.example.vitsi.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch

class ProfileVideoViewModel(
    private val userRepo: UserRepo = DefaultUserRepo()
): BaseViewModel() {

    private val _listOfRemoteVideo = MutableLiveData<List<RemoteVideo>>()
    val listOfRemoteVideo: LiveData<List<RemoteVideo>> = _listOfRemoteVideo

    fun fetchVideos(profileUid: String, videoType: VideoType) {
        viewModelScope.launch {
            val result = userRepo.getUserAudios(profileUid, videoType)
            _listOfRemoteVideo.value = result.tryData()?.filterNotNull()
        }
    }

}