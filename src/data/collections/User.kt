package com.sscott.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val email: String,
    val password: String,

    @BsonId
    val id: String = ObjectId().toString() //default value auto generated if none given
) {
}