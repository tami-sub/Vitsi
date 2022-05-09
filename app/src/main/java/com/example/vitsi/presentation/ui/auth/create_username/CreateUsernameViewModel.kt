package com.example.vitsi.presentation.ui.auth.create_username

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitsi.*
import com.example.vitsi.models.upload.Progress
import com.example.vitsi.repo.network.auth.AuthRepo
import com.example.vitsi.repo.network.name.NameRepo
import com.example.vitsi.repo.network.user.UserRepo
import com.example.vitsi.repo.network.name.DefaultNameRepo
import com.example.vitsi.repo.network.user.DefaultUserRepo
import com.example.vitsi.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateUsernameViewModel(
    private val nameRepo: NameRepo = DefaultNameRepo(),
    private val authRepo: AuthRepo = AuthRepo(),
    private val userRepo: UserRepo = DefaultUserRepo()
) : BaseViewModel(), NameRepo by nameRepo {

    lateinit var args: CreateUsernameFragmentArgs

    val liveUsername = MutableLiveData("")

    private val _errorTextRes = MutableLiveData<Int?>()
    val errorTextRes: LiveData<Int?> = _errorTextRes

    private val _progress = MutableLiveData(Progress.IDLE)
    val progress: LiveData<Progress> = _progress

    private val liveUsernameObserver: (String) -> Unit = {
        viewModelScope.launch { checkUsernameIsValid() }
    }

    init {
        liveUsername.observeForever(liveUsernameObserver)
    }

    fun setUp(navArgs: CreateUsernameFragmentArgs) {
        viewModelScope.launch {
            args = navArgs
            args.googleBody?.userName?.let { googleName ->
                getUsernameFromGoogleUsername(googleName)
            }
        }
    }

    fun completeSignIn() {
        _progress.value = Progress.ACTIVE
        viewModelScope.launch {
            // Username will not be null since we've already done our check. The safety check is because I dislike the non-null assertion mark
            val username = liveUsername.value ?: ""
            val authResult = getAuthResult()

            // To avoid if...else statements everywhere, I preferred using Uncle Bob's method of throwing exceptions
            try {
                val profilePicture = args.googleBody?.profilePicture
                val isSuccess = userRepo
                    .addUserToDatabase(username, authResult.forceData(), profilePicture)
                    .forceData()

                if (isSuccess) {
                    registerUserName(username)
                    _progress.value = Progress.DONE
                }
            } catch (e: Exception) {
                showMessage(R.string.error_during_account_creation)
                _progress.value = Progress.FAILED
            }
        }
    }

    private suspend fun getAuthResult() = when {
        args.credential != null -> {
            authRepo.signInWithCredential(args.credential!!)
        }
        else -> throw UnknownError("There is no credential or emailBody. Call the 911, a bug has occurred(pun intended)")
    }

    suspend fun checkUsernameIsValid(): Boolean {
        val errorRes = getErrorFromUsername(liveUsername.value ?: return false)
        _errorTextRes.value = errorRes
        return errorRes == null
    }

    override fun onCleared() {
        super.onCleared()
        liveUsername.removeObserver(liveUsernameObserver)
    }
}