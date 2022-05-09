package com.example.vitsi.repo.network.name

import com.example.vitsi.models.TheResult

interface NameRepo {
    /**
     * Checks for errors in the username and returns a string resource referencing the error.
     *
     * @return a string resource id that represents the error. Null means no error and the username is valid.
     */
    suspend fun getErrorFromUsername(username: String): Int?

    /**
     * Checks whether the name exists in the database
     *
     * @return true if the name has already been taken
     */
    suspend fun doesNameExist(username: String): Boolean

    /**
     * Registers the name to the Database
     */
    suspend fun registerUserName(username: String): TheResult<Boolean>

    /**
     * Generates a name from the user's google account name
     * @return A name derived from the user's google account name
     */
    suspend fun getUsernameFromGoogleUsername(googleName: String): String

    /**
     * Generates a truly random name
     */
    suspend fun generateRandomName(): String
}