package services

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import models.Group
import randomUUID
import screens.uid

object GroupService {

    suspend fun getGroups(userId: String): List<String> {
        return Firebase.firestore.collection("users/${userId}/groups/").get().documents.map { it.get("name") }
    }

    suspend fun createGroup(name: String): Group? {
        val group = Group(name = name, id = randomUUID())
        Firebase.firestore.document("users/${uid}/groups/${group.name}").set(Group.serializer(), group, encodeDefaults = true)
        return null
    }

    suspend fun joinGroup(groupKey: String) {

    }

    suspend fun deleteGroup(groupKey: String) {

    }
}