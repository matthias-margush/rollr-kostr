package kostr.test

import kostr.topic.Topic
import kostr.topic.builder.MockedTopicBuilderContext
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import java.util.*

private fun <K, V> TopologyTestDriver.toTopic(topic: Topic<K, V>): TestInputTopic<K, V> {
    return this.createInputTopic(
        topic.name,
        topic.keySerde.make(true).serializer(),
        topic.valueSerde.make(false).serializer(),
    )
}

private fun <K, V> TopologyTestDriver.fromTopic(topic: Topic<K, V>): TestOutputTopic<K, V> {
    return this.createOutputTopic(
        topic.name,
        topic.keySerde.make(true).deserializer(),
        topic.valueSerde.make(false).deserializer(),
    )
}

private fun props(props: Map<String, Any>): Properties {
    val properties = Properties()
    properties.putAll(props)
    return properties
}

fun withTopology(
    topology: (StreamsBuilder) -> Unit,
    block: (TopologyTestDriver) -> Unit
) {
    withTopology("", topology, block)
}

fun withTopology(
    context: String = "",
    topology: (StreamsBuilder) -> Unit,
    block: (TopologyTestDriver) -> Unit
) {
    val ctx = MockedTopicBuilderContext(context)
    Topic.context.withBinding(ctx).use {
        val builder = StreamsBuilder()
        topology(builder)
        val td = TopologyTestDriver(
            builder.build(),
            props(
                mapOf(
                    "application.id" to "kotlin-kafka-test",
                    "bootstrap.servers" to "localhost:9092", // (unused)
                    "schema.registry.url" to "mock://${ctx.scope}"
                )
            )
        )
        block(td)
    }
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
    return if (message != null) {
        Pair(message.key, message.value)
    } else {
        null
    }
}
