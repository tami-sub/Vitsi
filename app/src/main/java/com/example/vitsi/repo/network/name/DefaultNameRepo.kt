package com.example.vitsi.repo.network.name

import com.example.vitsi.R
import com.example.vitsi.repo.network.utils.FirePath
import com.example.vitsi.repo.network.utils.safeAccess
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class DefaultNameRepo(private val realFire: FirebaseDatabase = Firebase.database) : NameRepo {
    private val firePath = FirePath()

    override suspend fun getErrorFromUsername(username: String) = when {
        username.length < 4 -> R.string.short_username
        username.contains(" ") -> R.string.name_contains_spaces
        username.length >= 25 -> R.string.long_username
        doesNameExist(username) -> R.string.name_unavailable
        else -> null
    }

    override suspend fun doesNameExist(username: String) =
        realFire
            .getReference(firePath.getTakenUsernames(username))
            .get()
            .await()
            ?.exists() == true

    override suspend fun registerUserName(username: String) = safeAccess {
        realFire
            .getReference(firePath.getTakenUsernames(username))
            .setValue(username)
            .await()

        true
    }

    override suspend fun generateRandomName(): String {
        var randomUserName = ""
        val initialNameArray = listOf("user", "account", "person")

        while (doesNameExist(randomUserName) || randomUserName == "") {
            val randomNumber = Random.nextInt(100_000_000).toString()
            randomUserName = initialNameArray.random() + randomNumber
        }

        return randomUserName
    }

    override suspend fun getUsernameFromGoogleUsername(googleName: String): String {
        var userName = googleName

        while (doesNameExist(userName)) {
            val randomNumber = Random.nextInt(100_000_000).toString()
            userName = googleName + randomNumber
        }

        return userName
    }

}