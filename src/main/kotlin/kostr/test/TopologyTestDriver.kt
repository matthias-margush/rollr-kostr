package kostr.test

import kostr.Topic
import org.apache.kafka.streams.*
import java.util.*

private val inputTopicGenerators: MutableMap<Topic<*, *>, TestInputTopic<*, *>> = mutableMapOf()
private val outputTopicGenerators: MutableMap<Topic<*, *>, TestOutputTopic<*, *>> = mutableMapOf()

@Suppress("UNCHECKED_CAST")
private fun <K, V> TopologyTestDriver.toTopic(topic: Topic<K, V>): TestInputTopic<K, V> {
    return inputTopicGenerators.getOrPut(topic) {
        this.createInputTopic(
            topic.name,
            Topic.mockedSerde(topic.keySerde, true).serializer(),
            Topic.mockedSerde(topic.valueSerde, false).serializer()
        )
    } as TestInputTopic<K, V>
}

@Suppress("UNCHECKED_CAST")
private fun <K, V> TopologyTestDriver.fromTopic(topic: Topic<K, V>): TestOutputTopic<K, V> {
    return outputTopicGenerators.getOrPut(topic) {
        this.createOutputTopic(
            topic.name,
            Topic.mockedSerde(topic.keySerde, true).deserializer(),
            Topic.mockedSerde(topic.valueSerde, false).deserializer()
        )
    } as TestOutputTopic<K, V>
}

private fun props(props: Map<String, Any>): Properties {
    val properties = Properties()
    properties.putAll(props)
    return properties
}

fun withTopology(topology: (StreamsBuilder) -> Unit): TopologyTestDriver {
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
    this.toTopic(topic).pipeInput(k, v)
}

/**
 * Get the next message.
 */
fun <K, V> TopologyTestDriver.consume(topic: Topic<K, V>): Pair<K, V>? {
    val message = this.fromTopic(topic).readKeyValue()
    return if (message != null) { Pair(message.key, message.value) } else { null }
}

