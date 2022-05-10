package com.example.vitsi.presentation.ui.auth.sign_up

import androidx.lifecycle.LiveData
import com.example.vitsi.data.network.auth.AuthRepo
import com.example.vitsi.presentation.architecture.BaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpViewModel : BaseViewModel() {

    private val authRepo = AuthRepo()
    val googleAuthRepo = authRepo.GoogleAuthRepo()

    val liveCredential: LiveData<AuthCredential>
        get() = authRepo.liveCredential

    // If the user uses google sign in, this will have a value
    val googleAccount: GoogleSignInAccount?
        get() = authRepo.liveGoogleAccount.value

    private val mAuthListener = FirebaseAuth.AuthStateListener { p0 ->
    }

    init {
        Firebase.auth.addAuthStateListener(mAuthListener)
    }

    override fun onCleared() {
        super.onCleared()
        Firebase.auth.removeAuthStateListener(mAuthListener)
    }
}