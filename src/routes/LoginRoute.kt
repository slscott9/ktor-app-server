package com.sscott.routes

import com.sscott.data.checkPassworkForEmail
import com.sscott.data.requests.AccountRequest
import com.sscott.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
/*
    data class AccountRequest(
    val email: String,
    val password: String)

    Extract email and password from android app and send in post request.

    This loginRoute handles the post request.
    Tries to parse the request (json)  if successful we extract the email and password

 */
fun Route.loginRoute() {
    route("/login"){
        post {
            val request = try {
                call.receive<AccountRequest>() //parse the request from sender and assign it to request val

            }catch (e: ContentTransformationException){
                //what do we want to do if an invalid request comes in?
                //answer to that request and notify sender(client) that the request was invalid

                call.respond(HttpStatusCode.BadRequest)
                return@post //return out of post function because request was invalid
            }

            val isPasswordCorrect = checkPassworkForEmail(request.email, request.password)
            if(isPasswordCorrect){
                call.respond(OK, SimpleResponse(true, "You are now logged in"))
            }else {
                call.respond(OK, SimpleResponse(false, "The email or password is incorrect"))
            }
        }
    }
}