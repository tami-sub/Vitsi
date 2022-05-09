package com.example.vitsi.repo.network.tag

import com.example.vitsi.models.tag.Tag
import com.example.vitsi.repo.network.utils.FirePath
import com.example.vitsi.repo.network.utils.safeAccess
import com.example.vitsi.utils.runAsync
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class DefaultTagRepo(private val realFire: FirebaseDatabase = Firebase.database) : TagRepo {
    private val firePath = FirePath()

    override suspend fun fetchPopularTags() = safeAccess {
        val tagList = realFire
            .getReference(firePath.getTagsPath())
            .orderByChild("count")
            .limitToFirst(12)
            .get().await().getValue<List<Tag>>()

        return@safeAccess tagList ?: listOf()
    }

    override suspend fun fetchTagAudios(tagName: String) = safeAccess {
        realFire
            .getReference(firePath.getTagAudios(tagName))
            .limitToFirst(12)
            .get()
            .await()
            .getValue<List<String>>() ?: listOf()
    }

    override fun saveTagsInVideo(tags: Collection<String>, audioId: String) {
        tags.forEach {
            val tagName = it.replace("#", "", true)

            // Increment tag count
            realFire
                .getReference(firePath.getTagInfo(tagName))
                .runAsync {
                    // Get an existing tag or create a new one.
                    val tag = get().await().getValue<Tag>() ?: Tag(tagName, 0)
                    tag.count++

                    setValue(tag)
                }

            realFire
                .getReference(firePath.getTagAudios(tagName))
                .child(audioId)
                .setValue(audioId)
        }
    }
}