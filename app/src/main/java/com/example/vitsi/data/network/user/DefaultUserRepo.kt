package com.example.vitsi.data.network.user

import com.example.vitsi.domain.user.User
import com.example.vitsi.domain.audio.RemoteAudio
import com.example.vitsi.domain.audio.VideoType
import com.example.vitsi.data.network.utils.FirePath
import com.example.vitsi.data.network.utils.safeAccess
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

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
                }.await()
                .getValue<User>()

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
            allVideos.child(audioId).get().await().getValue<RemoteAudio>()
        }
    }
}