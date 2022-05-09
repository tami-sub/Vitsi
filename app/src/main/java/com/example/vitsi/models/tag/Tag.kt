package com.example.vitsi.models.tag

data class Tag(val name: String, var count: Int) {
    constructor(): this("", 0)
}
