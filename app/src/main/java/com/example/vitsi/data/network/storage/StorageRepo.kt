package com.example.vitsi.data.network.storage

import android.net.Uri
import com.example.vitsi.data.network.utils.safeAccess
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*

class StorageRepo {
    private val fireStorage = Firebase.storage

    suspend fun uploadVideo(localVideoUri: String?) = safeAccess {
        val storageRefer = fireStorage.getReference("audios/${Firebase.auth.uid}/${UUID.randomUUID()}")
        val uploadTask = storageRefer.putFile(Uri.fromFile(File(localVideoUri!!))!!).await()
        uploadTask.storage.downloadUrl.await()
    }

}