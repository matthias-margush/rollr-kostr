package kostr.serde

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import kostr.Mapified
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes


fun <T: SpecificRecord> AvroSerde(@Suppress("UNUSED_PARAMETER") _na: () -> Unit): SpecificAvroSerde<T> =
        SpecificAvroSerde()

fun StringSerde(@Suppress("UNUSED_PARAMETER") _na: () -> Unit): Serdes.StringSerde = Serdes.StringSerde()

fun <T: Mapified> ednSerde(newObject: (Map<String, Any?>) -> T): () -> Serde<T> =
    { -> EDNSerde(newObject) }
