package com.example.plugins

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import java.time.LocalDate
import java.util.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, cause.stackTraceToString())
        }
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(ContentNegotiation) {
        jackson {
//            setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
//            registerModule(JsonUtils.BIG_DECIMAL_2_STRING_MODULE)
//            enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
//            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            registerModule(JavaTimeModule())
//            registerModule(
//                KotlinModule.Builder()
//                    .withReflectionCacheSize(512)
//                    .configure(KotlinFeature.NullToEmptyCollection, false)
//                    .configure(KotlinFeature.NullToEmptyMap, false)
//                    .configure(KotlinFeature.NullIsSameAsDefault, true)
//                    .configure(KotlinFeature.SingletonSupport, true)
//                    .configure(KotlinFeature.StrictNullChecks, false)
//                    .build()
//            )
//            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/testReceive") {
            val s1 = System.currentTimeMillis()
            val receive = call.receive<List<RequestData>>()
            val s2 = System.currentTimeMillis()
            println("Receive spent ${s1 - s2}ms.")
            call.respond(receive)
        }
    }
}


data class RequestData(
    val id: String,
    val date: LocalDate,
    val value: Double
)