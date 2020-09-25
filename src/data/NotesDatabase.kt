package com.sscott.data

import com.sscott.data.collections.Note
import com.sscott.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue


/*
    contains kotlin functions that interact with database

    Because these functions are in a file not a class we can access them from anywhere in our application
 */

val client = KMongo.createClient().coroutine //makes sure kmongo uses coroutines for all database operations we define here

private val database = client.getDatabase("NotesDatabase")

//Collections are like our database tables
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

//inserts use into our User collection and returns true if the write was successful
suspend fun registerUser(user: User) : Boolean {
    return users.insertOne(user).wasAcknowledged()
}

/*
    querying for a user in mongo
    Query by User:: property eq emailQuery this says search the User collection and check if our parameter matches a User collection property
 */
suspend fun checkIfUserExists(emailQuery: String) : Boolean {

    //select * from user where email = :email
    return users.findOne(User::email eq emailQuery) != null //returns false if not found
}


/*
    Now that we have these two functions we ready to
    Write register endpoints or URL for which we are listening for registration requests
    From app we send http requests using retrofit to the url for data
 */

suspend fun checkPassworkForEmail(emailQuery: String, passwordToCheck: String) : Boolean {
    val actualPassword = users.findOne(User::email eq emailQuery)?.password ?: return false

    return actualPassword == passwordToCheck
}

suspend fun getNotesForUser(email: String): List<Note>{
    return notes.find(Note::owners contains email).toList()
}

suspend fun saveNote(note: Note) : Boolean {
    val noteExists = notes.findOneById(note.id) != null

    return if(noteExists){
        notes.updateOneById(note.id, note).wasAcknowledged()
    }else{
        notes.insertOne(note).wasAcknowledged()
    }
}
//we use an and query with comma Note::id eq , Note::owners contains email both conditions must be true

/*
    finds note with email and id

    if there is a note  and if the owners of the note are greater than 1.
    Remove the email from owner list
    update the notes owner list with the new owner list
    return the updateResult boolean

    otherwise if only one owner of the note delete that whole note

    if note is null then return false -> no note exists
 */
suspend fun deleteNoteForUser(email: String , noteId: String) : Boolean {
    val note = notes.findOne(Note::id eq noteId, Note::owners contains email)

    note?.let{
        if(it.owners.size > 1){ //if note has multiple owners just delete email from owners list
            val newOwners = note.owners - email //returns list without email

            val updateResult = notes.updateOne(Note::id eq note.id, setValue(Note::owners, newOwners))
            return updateResult.wasAcknowledged()
        }

        return notes.deleteOneById(note.id).wasAcknowledged()
    } ?: return false //no note with id or note that belongs to the email
}

/*
    Find the ownsers list for the note with noteID
    if no note with noteID return false

    otherwise return if the notes collection was updated
    we use setValue on the Note::owners and add the new owner to the note's list
 */
suspend fun addOwnerToNote(noteId: String, owner: String): Boolean {
    val owners = notes.findOneById(noteId)?.owners ?: return false
    return notes.updateOneById(noteId, setValue(Note::owners, owners + owner)).wasAcknowledged()
}

suspend fun isOwnerOfNote(noteId: String, owner: String) : Boolean {
    val note = notes.findOneById(noteId) ?: return false
    return owner in note.owners
}


