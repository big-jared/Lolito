package services

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import models.Group
import randomUUID
import screens.uid

object GroupService {
    suspend fun getActiveGroup(): Group? = withContext(Dispatchers.IO) {
        try {
            Firebase.firestore.document("/users/$uid").get().data<Group>().takeIf { it.groupId.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllGroups(): List<Group> = withContext(Dispatchers.IO) {
        try {
            Firebase.firestore.collection("users/$uid/groups").get().documents.map { it.data() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setGroupActive(group: Group) = withContext(Dispatchers.IO){
        Firebase.firestore.document("/users/$uid")
            .set(Group.serializer(), group, encodeDefaults = true)
    }

    suspend fun createGroup(name: String): Group = withContext(Dispatchers.IO) {
        val group = Group(groupName = name, groupId = randomUUID())
        Firebase.firestore.document("/groups/${group.groupId}").set(Group.serializer(), group, encodeDefaults = true)
        Firebase.firestore.document("/users/$uid/groups/${group.groupId}").set(Group.serializer(), group, encodeDefaults = true)
        group
    }

    suspend fun getActiveGroupPath(): String = "/groups/${getActiveGroup()?.groupId}"
}