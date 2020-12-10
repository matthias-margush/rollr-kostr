package com.acmeinc.underwriting

import mu.KotlinLogging
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder

val log = KotlinLogging.logger {}

fun main() {
    val builder = StreamsBuilder()
    for (topology in configs.topologies) {
        log.info("building topology: $topology")
        topology(builder)
    }

    log.info("Starting kafka streams")
    val streams = KafkaStreams(builder.build(), configs.kafka("loan-approver"))
    streams.start()
    log.info("Started kafka streams")
    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
