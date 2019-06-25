package kostr.client

import kostr.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.*

fun <K, V> KafkaSubscription(properties: Properties, topic: Topic<K, V>) : KafkaConsumer<K, V> {
    val consumer = KafkaConsumer(properties, topic.keySerde.deserializer(), topic.valueSerde.deserializer())
    consumer.subscribe(listOf(topic.name))
    return consumer
}

fun <K, V> KafkaConsumer<K, V>.seekToEnd() : KafkaConsumer<K, V> {
    this.poll(Duration.ZERO)
    val partitionAssignments = this.assignment()
    this.seekToEnd(partitionAssignments)
    for (partition in partitionAssignments) {
        this.position(partition)
    }
    return this
}

fun <K, V> KafkaConsumer<K, V>.messages(fuse: () -> Boolean): Iterator<ConsumerRecord<K, V>> {
    val consumer = this
    return iterator {
        while (fuse()) {
            val messages = consumer.poll(Duration.ofSeconds(1))
            for (message in messages) {
                yield(message)
            }
        }
    }
}

fun timer(durationMillis: Long): () -> Boolean {
    val endTime = System.currentTimeMillis() + durationMillis
    return { System.currentTimeMillis() < endTime }
}