package com.sscott.data.requests

//Used to add an owner to a note
data class AddOwnerRequest(
    val noteId: String,
    val owner: String
) {
}