package bts.api.tables

import bts.api.schema.TestTable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.DateTimeException


@Serializable
data class TestEntity(
    val date_field: String,
    val integer_number_field: Int,
    val float_number_field: Float,
    val boolean_field: Boolean,
    val text_field: String,
)

class TestService(database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(TestTable)
        }
    }


    @OptIn(FormatStringsInDatetimeFormats::class)
    suspend fun save(data: TestEntity): Int = newSuspendedTransaction {
        TestTable.insert {
            it[date_field] = LocalDate.parse(data.date_field,
                LocalDate.Format { byUnicodePattern("MM/dd/yyyy") })
            it[integer_number_field] = data.integer_number_field
            it[float_number_field] = data.float_number_field
            it[boolean_field] = data.boolean_field
            it[text_field] = data.text_field
        }[TestTable.id].value
    }

}





