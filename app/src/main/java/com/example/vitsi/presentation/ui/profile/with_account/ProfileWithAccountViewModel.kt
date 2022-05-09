package com.example.vitsi.presentation.ui.profile.with_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitsi.models.user.User
import com.example.vitsi.repo.network.user.DefaultUserRepo
import com.example.vitsi.utils.architecture.BaseViewModel
import kotlinx.coroutines.launch

class ProfileWithAccountViewModel: BaseViewModel() {
    private val userRepo = DefaultUserRepo()

    private val _profileUser = MutableLiveData<User>()
    val profileUser: LiveData<User> = _profileUser

    fun fetchUser(profileUid: String) {
        viewModelScope.launch {
            val user = userRepo.getUserProfile(profileUid).tryData() ?: return@launch
            _profileUser.value = user
        }
    }

}