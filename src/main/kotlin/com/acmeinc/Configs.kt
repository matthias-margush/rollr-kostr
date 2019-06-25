package com.acmeinc

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig.*

import java.lang.System.getenv
import java.util.*

object configs {
    fun kafka(applicationId: String): Properties {
        val props = Properties()
        props[APPLICATION_ID_CONFIG] = getenv("APPLICATION_ID") ?: applicationId
        props[BOOTSTRAP_SERVERS_CONFIG] = getenv("BOOTSTRAP_SERVERS") ?: "localhost:9092"
        props[COMMIT_INTERVAL_MS_CONFIG] = getenv("COMMIT_INTERVAL_MS") ?: "10"
        return props
    }

    var topologies: List<(StreamsBuilder) -> Unit> = listOf(::loanApprovalTopology)
}
