package com.example.vitsi.data.network.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vitsi.data.network.utils.safeAccess
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthRepo(
    private val fireAuth: FirebaseAuth = Firebase.auth
) {

    val liveGoogleAccount = MutableLiveData<GoogleSignInAccount>()

    private val _liveCredential = MutableLiveData<AuthCredential>()
    val liveCredential: LiveData<AuthCredential> = _liveCredential


    suspend fun signInWithCredential(credential: AuthCredential) = safeAccess {
        fireAuth.signInWithCredential(credential).await()
    }
    inner class GoogleAuthRepo {
        private lateinit var googleSignInClient: GoogleSignInClient

        fun doGoogleAuth(context: Context, launcher: ActivityResultLauncher<Intent>) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("313170957114-atjjnmjb5f1ip3i4n17bokkfk110rbl4.apps.googleusercontent.com")
                .requestProfile()
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        }

        fun handleGoogleOnResult(data: Intent?) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task?.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                Timber.d("middle of handleGoogleOnResult with credential is $credential")

                liveGoogleAccount.value = account ?: return
                _liveCredential.value = credential
            } catch (apiException: ApiException) { // Thrown when the user exits the sign up process midway
                Timber.e(apiException, "Caught exception")
            }
        }
    }
}