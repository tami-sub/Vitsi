package com.example.vitsi.utils

import java.util.concurrent.TimeUnit

object TimeUtils {

    fun convertTimeToDisplayTime(timeInMillis: Long): String {
        val time = String.format("%d:%d",
            TimeUnit.MILLISECONDS.toMinutes(timeInMillis),
            TimeUnit.MILLISECONDS.toSeconds(timeInMillis) -
                    TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes(timeInMillis)))
        )
        return time
    }

}