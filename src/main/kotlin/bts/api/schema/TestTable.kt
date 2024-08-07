package bts.api.schema

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object TestTable : IntIdTable() {
    val date_field = date("date_field")
    val integer_number_field = integer("integer_number_field")
    val float_number_field = float("float_number_field")
    val boolean_field = bool("boolean_field")
    val text_field = text("text_field")
}