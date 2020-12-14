package kostr.topic.builder

import org.apache.kafka.common.serialization.Serde

interface SerdeBuilder<T> {
    fun make(isKey: Boolean): Serde<T>
}

class IdentitySerdeBuilder<T>(private val serde: Serde<T>) : SerdeBuilder<T> {
    override fun make(isKey: Boolean): Serde<T> = serde
}

