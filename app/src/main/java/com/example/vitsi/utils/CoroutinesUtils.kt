package com.example.vitsi.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun <T> T.runAsync(
    runOnUIThread: Boolean = false,
    crossinline lambda: suspend T.() -> Unit
) {
    GlobalScope.launch(if (runOnUIThread) Dispatchers.Main else Dispatchers.IO) {
        lambda(this@runAsync)
    }
}

suspend inline fun <T, A> T.letAsync(
    crossinline lambda: suspend T.() -> A
) = lambda(this)