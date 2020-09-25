package com.sscott.routes

import com.sscott.data.checkIfUserExists
import com.sscott.data.collections.User
import com.sscott.data.registerUser
import com.sscott.data.requests.AccountRequest
import com.sscott.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/*
    Send request to ipaddres/register

    1. route functions allows us to listen for post or get http requests
    2. to be able to parse the request which is json we need data classes for a response and request
    3. logic if the request to register user is invalid throw exception. catch block will response with http bad request and return.
        if request is valid then request variable is an AccountRequest object (email and password fields)

        Note that everything in the post block are coroutines. Ktor handles these for us. We can make a lot of requests at the same time because they are
        run in parallel by ktor

      4. Success request we check if user exists, if they do not exist

 */


fun Route.registerRoute() {
    route("/register"){
        post {
            val request = try {
                call.receive<AccountRequest>() //parse the request from sender and assign it to request val

            }catch (e: ContentTransformationException){
                //what do we want to do if an invalid request comes in?
                //answer to that request and notify sender(client) that the request was invalid

                call.respond(BadRequest)
                return@post //return out of post function because request was invalid
            }

            //if user request was valid we check if user exists in our database
            val userExists = checkIfUserExists(request.email) //functions from application file check db for user with email
            if(!userExists){
                if(registerUser(User(request.email, request.password))){
                    call.respond(OK, SimpleResponse(true, "Successfully created account")) //what our android app uses to display a toast with
                }else{
                    call.respond(OK, SimpleResponse(false, "An unknown error occured"))
                }
            }else {
                call.respond(OK, SimpleResponse(false, "User with that email already exists"))
            }
        }
    }
}

