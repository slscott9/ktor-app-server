package com.sscott

import com.sscott.data.checkPassworkForEmail
import com.sscott.data.collections.User
import com.sscott.data.registerUser
import com.sscott.routes.loginRoute
import com.sscott.routes.noteRoute
import com.sscott.routes.registerRoute
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.Routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*
    Ktor important concept are features they are used as server configurations
    The go inside the extension function of Application
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args) //launches main function of ktor server

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders) //causes ktor to append day to responses from this ktor server
    install(CallLogging) //logs all http requests and responses

    //specify what type of content we want this ktor server to send
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting() //json printed in nice formatted way
        }
    }

    install(Authentication){
        configureAuth()
    }

    //needs to go after authentication or will crash server
    install(Routing){
        registerRoute() //enables our post register user extention function from RegisterRount.kt
        loginRoute() //handles user login validation
        noteRoute() //uses authentication to get notes for a user with get request

    } //enables url endpoints which makes ktor server the actual rest api
}

//logic for how we authenticate users
private fun Authentication.Configuration.configureAuth() {
    basic {
        realm = "Note Server"

        validate { credentials ->
            //check name and password and logic if we auth user or not
            val email = credentials.name
            val password = credentials.password
            if(checkPassworkForEmail(email, password)){
                UserIdPrincipal(email) //keeps track of who is authenticated so we can get notes for that user

            }else null
        }
    }
}

