package com.sscott.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/*
    For Mongo db a primary key is usally a String that mongo randomly generates of length 25
 */
data class Note(
    val title: String,
    val content: String,
    val date: Long, //will be a time stamp this will let us sort or data efficiently
    val owners: List<String>, //shares notes with other uses by email
    val color: String,

    @BsonId
    val id: String = ObjectId().toString() //primary key gets generated by default

) {
}