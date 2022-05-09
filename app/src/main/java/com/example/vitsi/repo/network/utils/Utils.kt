package com.example.vitsi.repo.network.utils

import com.example.vitsi.models.TheResult.Companion.theError
import com.example.vitsi.models.TheResult.Companion.theSuccess
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue
import timber.log.Timber

suspend fun <T> safeAccess(firebaseLambda: suspend () -> T) = try {
    theSuccess(firebaseLambda())
} catch (e: Exception) {
    theError(e)
}