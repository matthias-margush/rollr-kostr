package kostr.serde

import com.google.common.base.CaseFormat.LOWER_CAMEL
import com.google.common.base.CaseFormat.LOWER_HYPHEN
import kostr.Mapified
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import us.bpsm.edn.Keyword
import us.bpsm.edn.TaggedValue
import us.bpsm.edn.parser.Parser
import us.bpsm.edn.parser.Parsers
import us.bpsm.edn.printer.Printers


class EDNFormatException(message: String) : RuntimeException(message)

class EDNDeserializer<T>(private val dataObject: (Map<String, Any?>) -> T) : Deserializer<T> {
    override fun configure(configs: Map<String, *>, isKey: Boolean) = Unit
    override fun close() = Unit
    override fun deserialize(topic: String, data: ByteArray): T {
        return dataObject(parseMap(data.toString(Charsets.UTF_8)))
    }
}

class EDNSerializer<T : Mapified> : Serializer<T> {
    override fun configure(configs: Map<String, *>, isKey: Boolean) = Unit
    override fun close() = Unit
    override fun serialize(topic: String, data: T): ByteArray {
        val m = data.map.entries.associate { (k, v) ->
            LOWER_CAMEL.to(LOWER_HYPHEN, k) to v
        }
        val d = Printers.printString(Printers.defaultPrinterProtocol(), m)
        return d.toByteArray(Charsets.UTF_8)
    }
}


class EDNSerde<T : Mapified>(private val newObject: (Map<String, Any?>) -> T) : Serde<T> {
    override fun configure(configs: Map<String, *>, isKey: Boolean) = Unit
    override fun close() = Unit
    override fun deserializer(): Deserializer<T> = EDNDeserializer(newObject)
    override fun serializer(): Serializer<T> = EDNSerializer()
}

/**
 * Deserializes an EDN fooSerde into a map with camel-cased, de-namespaced keys.
 *
 * Throws an EDNFormatException if edn is not exactly a single, non-nested, top-level map.
 */
fun parseMap(edn: String): Map<String, Any?> {
// fun <T> parseMap(edn: String) : T { // Map<String, Any?> {
    val parsableEdn = Parsers.newParseable(edn)
    val parser = Parsers.newParser(Parsers.defaultConfiguration())
    val ednMap = parser.nextValue(parsableEdn)

    if (ednMap == Parser.END_OF_INPUT) {
        throw EDNFormatException("EDN input is empty: $edn")
    }

    val kotlinMap = when (ednMap) {
        Parser.END_OF_INPUT -> throw EDNFormatException("EDN input is empty")
        is Map<*, *> -> {
            if (parser.nextValue(parsableEdn) != Parser.END_OF_INPUT) {
                throw EDNFormatException("EDN input contains > 1 top-level forms: $edn")
            }

            ednMap.entries.associate { (k_, v_) ->
                val k = if (k_ is Keyword) k_.name else k_.toString()
                val v = if (v_ is TaggedValue) v_.value else v_
                if (v is Collection<*> || v is Map<*, *>) throw EDNFormatException("EDN is nested: $edn")
                LOWER_HYPHEN.to(LOWER_CAMEL, k.toLowerCase()) to v
            }
        }
        else -> throw EDNFormatException("EDN form is not a map: $edn")
    }

    if (kotlinMap.size != ednMap.size) {
        throw EDNFormatException("EDN contains conflicting keys when keys are de-namespaced: $edn")
    }

    return kotlinMap
}