package kostr.client

import kostr.Topic
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.*

fun <K, V> KafkaProducer(properties: Properties, topic: Topic<K, V>) : KafkaProducer<K, V> {
    return KafkaProducer<K, V>(properties, topic.keySerde.serializer(), topic.valueSerde.serializer())
}