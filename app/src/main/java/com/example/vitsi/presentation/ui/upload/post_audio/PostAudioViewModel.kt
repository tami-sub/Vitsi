package com.example.vitsi.presentation.ui.upload.post_audio

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitsi.R
import com.example.vitsi.domain.local.LocalVideo
import com.example.vitsi.domain.succeeded
import com.example.vitsi.domain.upload.Progress
import com.example.vitsi.data.network.storage.StorageRepo
import com.example.vitsi.data.network.videos.DefaultVideosRepo
import com.example.vitsi.presentation.architecture.BaseViewModel
import kotlinx.coroutines.launch

class PostAudioViewModel : BaseViewModel() {

    private val storageRepo = StorageRepo()
    private val audiosRepo = DefaultVideosRepo()

    val liveDescription = MutableLiveData("")

    private val _uploadStatus = MutableLiveData(Progress.IDLE)
    val uploadStatus: LiveData<Progress> = _uploadStatus

    fun postAudio(context: Context, localAudio: LocalVideo) {
        val descriptionText = liveDescription.value ?: run {
            showMessage(R.string.audio_description_needed)
            return
        }

        _uploadStatus.value = Progress.ACTIVE

        viewModelScope.launch {

            val result = storageRepo.uploadAudio(localAudio.filePath)
            if (!result.succeeded) {
                _uploadStatus.value = Progress.FAILED
                Log.d("joka", "S H I I I T")
                showMessage(R.string.error_occurred_during_audio_upload)
                return@launch
            }

            Log.d("joka", result.tryData().toString())
            val downloadUrl = result.tryData()?.toString() ?: return@launch

            audiosRepo.saveVideoToFireDB(
                isPrivate = false,
                videoUrl = downloadUrl,
                descriptionText = descriptionText,
                duration = localAudio.duration,
                onComplete = { succeeded ->
                    _uploadStatus.value = if (succeeded) Progress.DONE else Progress.FAILED
                }
            )
        }
    }
}