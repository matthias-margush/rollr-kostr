package kostr

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord

data class Topic<K, V>(val name: String,
                       val keySerde: Serde<K>,
                       val valueSerde: Serde<V>) {
    fun consumed(): Consumed<K, V> = consumed(keySerde, valueSerde)
    fun produced(): Produced<K, V> = produced(keySerde, valueSerde)

    companion object {
        var useMocking = false
        private val mockSchemaRegistryClient = MockSchemaRegistryClient()

        fun <T> mockedSerde(serde: Serde<T>, isKey: Boolean): Serde<T> {
            if (serde is SpecificAvroSerde) {
                val mockedSerde: SpecificAvroSerde<SpecificRecord> = SpecificAvroSerde(mockSchemaRegistryClient)
                mockedSerde.configure(mapOf(
                    KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS to true,
                    KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG to "unused",
                    KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG to true
                ), isKey)

                @Suppress("UNCHECKED_CAST")
                return mockedSerde as Serde<T>
            }
            return serde
        }

        private fun <K, V> consumed(keySerde: Serde<K>, valueSerde: Serde<V>): Consumed<K, V> {
            return if (useMocking) {
                Consumed.with(mockedSerde(keySerde, true), mockedSerde(valueSerde, false))
            } else {
                Consumed.with(keySerde, valueSerde)
            }
        }

        private fun <K, V> produced(keySerde: Serde<K>, valueSerde: Serde<V>): Produced<K, V> {
            return if (useMocking) {
                Produced.with(mockedSerde(keySerde, true), mockedSerde(valueSerde, false))
            } else {
                Produced.with(keySerde, valueSerde)
            }
        }
    }
}


