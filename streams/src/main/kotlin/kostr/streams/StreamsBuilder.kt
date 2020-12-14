package kostr.streams

import kostr.topic.Topic
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced

fun <K, V> StreamsBuilder.stream(
    topic: Topic<K, V>,
): KStream<K, V> =
    stream(
        topic.name,
        Consumed.with(
            topic.keySerde.make(true),
            topic.valueSerde.make(false),
        ),
    )

infix fun <K, V> KStream<K, V>.to(topic: Topic<K, V>) =
    to(
        topic.name,
        Produced.with(
            topic.keySerde.make(true),
            topic.valueSerde.make(false),
        ),
    )

infix fun <K, V, VR> KStream<K, V>.mapValues(mapper: (v: V) -> VR): KStream<K, VR> = this.mapValues(mapper)
