package kostr.topic

import kostr.topic.builder.DefaultTopicBuilderContext
import kostr.topic.builder.SerdeBuilder
import kostr.topic.builder.IdentitySerdeBuilder
import kostr.topic.builder.TopicBuilderContext
import org.apache.kafka.common.serialization.Serde
import scope.Dynamic

data class Topic<K, V>(
    val name: String,
    val keySerde: SerdeBuilder<K>,
    val valueSerde: SerdeBuilder<V>,
) {
    companion object BuilderContext {
        val context = Dynamic.binding<TopicBuilderContext>(DefaultTopicBuilderContext)
    }

    constructor(
        name: String,
        keySerde: Serde<K>,
        valueSerde: Serde<V>
    ) :
            this(
                name,
                IdentitySerdeBuilder(keySerde),
                IdentitySerdeBuilder(valueSerde)
            )

    constructor(
        name: String,
        keySerde: Serde<K>,
        valueSerdeBuilder: SerdeBuilder<V>
    ) :
            this(
                name,
                IdentitySerdeBuilder(keySerde),
                valueSerdeBuilder
            )

    constructor(
        name: String,
        keySerdeBuilder: SerdeBuilder<K>,
        valueSerde: Serde<V>
    ) :
            this(
                name,
                keySerdeBuilder,
                IdentitySerdeBuilder(valueSerde)
            )
}



