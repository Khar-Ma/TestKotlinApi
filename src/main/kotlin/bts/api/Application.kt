package bts.api

import bts.api.tables.TestService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    val database = Database.connect(
        url = "jdbc:mysql://bts-db-test.cvoec4ukmsab.eu-north-1.rds.amazonaws.com:3306/bts_db_test",
        user = "admin",
        password = "00000000",
        driver = "com.mysql.cj.jdbc.Driver",
    )

    val service = TestService(database)
    install(Routing) {
        test(service)
    }
}
