# rollr-kostr

![rollr-kostr](rollercoaster-297308_640.png)
<!-- (Image from https://pixabay.com/vectors/rollercoaster-roller-coaster-297308/) -->

This project implements five small conveniences for using kafka streams with
kotlin.

## Through the Corkscrew

### 🎢 Topics & Serdes

A [`Topic`](client/src/main/kotlin/kostr/topic/Topic.kt) abstraction that
packages up the topic name, key serde and value serde.

```kotlin
val LoanApproval = Topic<String, LoanApproval>(
    "loan-approval",
    Serdes.String(),
    AvroSerde<LoanApproval>(),
)
```

This defines a topic named "loan-approval" using the string serde for the key,
and whose value uses the avro serde.

### 🎢 Building Topologies

The kafka streams builder API
is [extended](streams/src/main/kotlin/kostr/streams/StreamsBuilder.kt)
to support the `Topic` abstraction (using [Kotlin extension functions](https://kotlinlang.org/docs/reference/extensions.html)).

Here is an [example topology](examples/underwriting/src/main/kotlin/com/acmeinc/underwriting/LoanApprovalTopology.kt)
using the `LoanApproval` topic:

```kotlin
fun loanApprovalTopology(builder: StreamsBuilder) =
    builder.stream(LoanApplication)
        .mapValues(::rubberStamp) to LoanApproval
```

### 🎢 Tests

A few conveniences for testing. Schema registry is automatically mocked behind
the scenes. Topics defined using the enhanced [AvroSerde](avro-serde/src/main/kotlin/kostr/streams/serdes/avro/Avro.kt) 
will automatically utilize the mock registry when used within a test context.

```kotlin
@Test
fun `when applied for, loans are approved`() =
    withTopology(::loanApprovalTopology) { topology ->
        val application = LoanApplication.generate()

        topology.produce(Topics.LoanApplication, application)

        val approval = topology.consume(Topics.LoanApproval)
        assertNotNull(approval)

        val (id, details) = approval
        println("id: $id, details: $details")
    }
```

### 🎢 Pass-through Data

If unknown fields should be propagated through a topology (to implement a 
forward-compatibility data strategy), make it [Mapified](core/src/main/kotlin/kostr/Mapified.kt).

```kotlin
data class LoanApplication(override val map: Map<String, Any?>) : Mapified {
    val id: UUID by map
    val firstName: String by map
    val lastName: String by map
    val income: Long by map
}
```

### 🎢 EDN Serde

A very basic EDN serde.
- Leverages [edn-java](https://github.com/bpsm/edn-java)
- [Basic kafka serde](edn-serde/src/main/kotlin/kostr/serde/Edn.kt) handles maps, rejects
  any other kind of EDN string (ready to be enhanced further as needed).

(adding a change here to test code climate trigger.)
(another change)
