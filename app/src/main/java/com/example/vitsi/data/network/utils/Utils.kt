package com.example.vitsi.data.network.utils

import com.example.vitsi.domain.TheResult.Companion.theError
import com.example.vitsi.domain.TheResult.Companion.theSuccess


suspend fun <T> safeAccess(firebaseLambda: suspend () -> T) = try {
    theSuccess(firebaseLambda())
} catch (e: Exception) {
    theError(e)
}