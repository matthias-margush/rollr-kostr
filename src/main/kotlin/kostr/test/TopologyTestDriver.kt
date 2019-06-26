package kostr.test

import kostr.Topic
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import java.util.*

private val topicGenerators: MutableMap<Topic<*, *>, ConsumerRecordFactory<*, *>> = mutableMapOf()

@Suppress("UNCHECKED_CAST")
private fun <K, V> topicGenerator(topic: Topic<K, V>): ConsumerRecordFactory<K, V> {
    return topicGenerators.getOrPut(topic) {
        ConsumerRecordFactory(
            topic.name,
            topic.keySerde.serializer(),
            topic.valueSerde.serializer(),
            0,
            1)
    } as ConsumerRecordFactory<K, V>
}

private fun props(props: Map<String, Any>): Properties {
    val properties = Properties()
    properties.putAll(props)
    return properties
}

fun mocktop(topology: (StreamsBuilder) -> Unit): TopologyTestDriver {
    Topic.useMocking = true
    val builder = StreamsBuilder()
    topology(builder)
    return TopologyTestDriver(
        builder.build(),
        props(
            mapOf(
                "application.id" to "kotlin-kafka-test",
                "bootstrap.servers" to "localhost:9092",
                "schema.registry.url" to "localhost:8081"
            )
        )
    )
}


/**
 * Produce a message.
 */
fun <K, V> TopologyTestDriver.produce(topic: Topic<K, V>, kv: Pair<K, V>) {
    val (k, v) = kv
    this.pipeInput(topicGenerator(topic).create(k, v))
}

data class Message<K, V>(val key: K, val value: V)

/**
 * Get the next message.
 */
fun <K, V> TopologyTestDriver.consume(topic: Topic<K, V>): Message<K, V>? {
    val message: ProducerRecord<K, V>? = this.readOutput(
        topic.name,
        Topic.mockedSerde(topic.keySerde, true).deserializer(),
        Topic.mockedSerde(topic.valueSerde, false).deserializer()
    )
    return if (message != null) { Message(key = message.key(), value = message.value()) } else { null }
}

