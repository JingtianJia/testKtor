package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import com.example.plugins.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.common.collect.Lists
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun testReceive() {
        val client = HttpClient(CIO) {
            install(HttpTimeout)
        }
        val date = LocalDate.now()
        val dataList = (1..100000).map { RequestData("$it", date, it.toDouble()) }
        val partition = Lists.partition(dataList, 50)
        println("构建完成")
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        for (requestParam in partition) {
            runBlocking {
                client.post("http://localhost:8080/testReceive") {
                    contentType(ContentType.Application.Json)
                    setBody(mapper.writeValueAsString(requestParam))
                    timeout {
                        requestTimeoutMillis = 30000
                        connectTimeoutMillis = 30000

                    }
                }
            }
        }
    }

}
