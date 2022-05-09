package com.example.vitsi.presentation.ui.auth.log_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.vitsi.R
import com.example.vitsi.models.succeeded
import com.example.vitsi.repo.network.auth.AuthRepo
import com.example.vitsi.utils.architecture.BaseViewModel
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.launch

class LogInViewModel : BaseViewModel() {

    private val authRepo = AuthRepo()
    val googleAuthRepo = authRepo.GoogleAuthRepo()

    private val _navigateToMyProfile = MutableLiveData(false)
    val navigateToMyProfile: LiveData<Boolean> = _navigateToMyProfile.distinctUntilChanged()

    private val liveCredentialObserver: (AuthCredential?) -> Unit = { credential ->
        credential?.let {
            viewModelScope.launch {
                val result = authRepo.signInWithCredential(credential)
                if (!result.succeeded)
                    showMessage(R.string.error_occurred_during_log_in)

                _navigateToMyProfile.value = result.succeeded
            }
        }
    }

    init {
        authRepo.liveCredential.observeForever(liveCredentialObserver)
    }

    fun resetNavigateToMyProfile() {
        _navigateToMyProfile.value = false
    }

    override fun onCleared() {
        super.onCleared()
        authRepo.liveCredential.removeObserver(liveCredentialObserver)
    }
}