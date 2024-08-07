package com.bts.api

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import java.net.InetSocketAddress
import java.sql.*
import java.text.SimpleDateFormat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

fun main() {
    println("start")
    val server = HttpServer.create(InetSocketAddress(8080), 0)
    val threadPool = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    server.createContext("/api/v2/test", RoutingHandler())
    server.executor = threadPool.asExecutor()
    server.start()

    val latch = CountDownLatch(1)
    latch.await()
    println("end")
}

data class TestEntity(
    val date_field: String,
    val integer_number_field: Int,
    val float_number_field: Float,
    val boolean_field: Boolean,
    val text_field: String
)

class RoutingHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        if (exchange.requestMethod == "POST" && exchange.requestURI.path == "/api/v2/test") {
            try {
                val requestBody = exchange.requestBody.bufferedReader().use { it.readText() }
                val gson = Gson()
                val data = gson.fromJson(requestBody, TestEntity::class.java)

                val connection = getConnection()
                val id = saveData(connection, data)
                connection.close()

                val responseString = "saved with id: $id"
                val response = responseString.toByteArray()
                println("response: $responseString")

                exchange.sendResponseHeaders(202, response.size.toLong())
                exchange.responseBody.write(response)
                exchange.responseBody.close()
            } catch (e: Throwable) {
                exchange.responseBody.write("400 Bad Request, ${e.cause}".toByteArray())
                exchange.responseBody.close()
            }
        } else {
            exchange.sendResponseHeaders(404, 0)
            exchange.responseBody.close()
        }
    }

    private fun getConnection(): Connection {
        val url = "jdbc:mysql://bts-db-test.cvoec4ukmsab.eu-north-1.rds.amazonaws.com:3306/bts_db_test"
        val username = "admin"
        val password = "00000000"
        return DriverManager.getConnection(url, username, password)
    }

    private fun saveData(connection: Connection, data: TestEntity): Int? {
        val formattedDate = SimpleDateFormat("MM/dd/yyyy").parse(data.date_field)
        val sqlDate = Date(formattedDate.time)

        val query = """
            INSERT INTO Test (date_field, integer_number_field, float_number_field, boolean_field, text_field)
            VALUES ("$sqlDate",${data.integer_number_field},${data.float_number_field},${data.boolean_field},"${data.text_field}")
        """
        val statement: PreparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
        statement.executeUpdate()

        val generatedKeys: ResultSet = statement.generatedKeys
        val generatedId: Int? = if (generatedKeys.next()) {
            generatedKeys.getInt(1)
        } else {
            null
        }
        generatedKeys.close()
        statement.close()

        return generatedId
    }
}

