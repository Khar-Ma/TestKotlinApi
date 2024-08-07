package bts.api

import bts.api.tables.TestEntity
import bts.api.tables.TestService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.test(service: TestService) {
    post("api/v1/test") {
        val body = call.receive(TestEntity::class)
        val id = service.save(body)
        call.respondText(text = "data successfully added to database with id: $id", status = HttpStatusCode.Accepted)
    }
}