package kostr.streams.serdes.avro

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.*
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import kostr.topic.builder.SerdeBuilder
import kostr.topic.Topic
import kostr.topic.builder.DefaultTopicBuilderContext
import kostr.topic.builder.MockedTopicBuilderContext
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde

class AvroSerde<T : SpecificRecord>() : SerdeBuilder<T> {
    override fun make(isKey: Boolean): Serde<T> {
        when (val ctx = Topic.context.bound()) {
            is DefaultTopicBuilderContext -> return SpecificAvroSerde<T>()
            is MockedTopicBuilderContext -> {
                val mockedSerde: SpecificAvroSerde<SpecificRecord> = SpecificAvroSerde()
                mockedSerde.configure(
                    mapOf(
                        AUTO_REGISTER_SCHEMAS to true,
                        SCHEMA_REGISTRY_URL_CONFIG to "mock://${ctx.scope}",
                        SPECIFIC_AVRO_READER_CONFIG to true
                    ), isKey
                )

                @Suppress("UNCHECKED_CAST")
                return mockedSerde as Serde<T>
            }
        }
    }
}
