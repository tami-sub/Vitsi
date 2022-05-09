package com.example.vitsi.repo.network.user

import com.example.vitsi.models.user.User
import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.models.video.VideoType
import com.example.vitsi.repo.network.utils.FirePath
import com.example.vitsi.repo.network.utils.safeAccess
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultUserRepo(
    private val fireAuth: FirebaseAuth = Firebase.auth,
    private val firePath: FirePath = FirePath(),
    private val realFire: FirebaseDatabase = Firebase.database
) : UserRepo {

    override fun doesDeviceHaveAnAccount() = fireAuth.currentUser != null

    override suspend fun getUserProfile(uid: String?) = safeAccess {
            val userProfile = realFire
                .getReference(firePath.getUserInfo(uid ?: ""))
                .get()
                .addOnCompleteListener {
                    val user = it.result.getValue<User>()
                    Timber.d("user is $user")
                }.await()
                .getValue<User>()

            Timber.d("userProfile is $userProfile")
            userProfile
    }

    override suspend fun addUserToDatabase(
        username: String,
        authResult: AuthResult,
        googleProfilePicture: String?
    ) =
        safeAccess {
            val user = User(
                username = username,
                totalLikes = 0,
                profilePictureUrl = googleProfilePicture,
                uid = authResult.user?.uid.toString()
            )

            realFire
                .getReference(firePath.getUserInfo(Firebase.auth.uid ?: ""))
                .setValue(user)
                .await()

            true
        }

    override suspend fun getUserAudios(uid: String?, videoType: VideoType) = safeAccess {
        val listOfUseraudioId = realFire
            .getReference(firePath.getUserAudios(uid ?: "", videoType))
            .get()
            .await()
            .getValue<Map<String, String>>()
            ?.values
            ?.toList() ?: listOf()

        val allVideos = realFire.getReference(firePath.getAllAudiosPath())
        listOfUseraudioId.map { audioId ->
            allVideos.child(audioId).get().await().getValue<RemoteVideo>()
        }
    }
}