package com.example.vitsi.repo.network.tag

import com.example.vitsi.models.TheResult
import com.example.vitsi.models.tag.Tag

interface TagRepo {
    fun saveTagsInVideo(tags: Collection<String>, audioId: String)

    suspend fun fetchPopularTags(): TheResult<List<Tag>>

    suspend fun fetchTagAudios(tagName: String): TheResult<List<String>>
}