package kostr.streams

import kostr.Topic
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream

infix fun <K, V> StreamsBuilder.stream(topic: Topic<K, V>): KStream<K, V>
        = stream(topic.name, topic.consumed())

infix fun <K, V> KStream<K, V>.to(topic: Topic<K, V>)
        = to(topic.name, topic.produced())

infix fun <K, V, VR> KStream<K, V>.mapValues(mapper: (v: V) -> VR): KStream<K, VR>
        = this.mapValues(mapper)
