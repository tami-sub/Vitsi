package com.example.vitsi.utils

object NumbersUtils {

    fun formatCount(count: Int): String {
        val stringCount = count.toString()
        return when {
            count > 1_000_000_000 -> "${stringCount[0]}.${stringCount[1]}B"
            count > 100_000_000 -> "${stringCount.take(3)}.${stringCount[3]}M"
            count > 10_000_000 -> "${stringCount.take(2)}.${stringCount[2]}M"
            count > 1_000_000 -> "${stringCount[0]}.${stringCount[1]}M"
            count > 100_000 -> "${stringCount.take(3)}.${stringCount[3]}K"
            count > 10_000 -> "${stringCount.take(2)}.${stringCount[2]}K"
            count > 1_000 -> "${stringCount[0]}.${stringCount[1]}K"
            else -> stringCount
        }
    }

}