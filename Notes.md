# Notes

## Pass Through Data
If a topology wants to carry along and propagate unknown fields through a topology (for forward-compatibility), a [map-backed data class](src/main/kotlin/com/acmeinc/LoanApplication.kt) is suitable.

## Working With Avro
- In this experiment a Java data class is generated from an avro spec as part of the build using the [gradle-avro-plugin](https://github.com/commercehub-oss/gradle-avro-plugin). (A generated Kotlin data class would be nicer, but using a Java class is seamless from Kotlin.)

## Working With EDN
- (I wouldn't suggest making EDN a primary format when working w/Kotlin, but it's not hard if needed.)
- Leverages [edn-java](https://github.com/bpsm/edn-java)
- Implemented [basic kafka serde](src/main/kotlin/kostr/serde/Edn.kt)
    - Handles maps, rejects any other kind of EDN string (enhance as needed)
- Implementing `kotlinx.serialization` would be good.

## Defining Topics
Topics & serdes go together conceptually, but the Java kafka APIs don't bundle topics & serdes in a data structure. Tracking those all separately can be a bit typo-prone if there are more than a few topics. The project simplifies this by defining a `Topic` type that packages up [topics](src/main/kotlin/kostr/Topic.kt) and [serdes](src/main/kotlin/kostr/serde/Serde.kt) together. Define a topic like this:

```kotlin
val LoanApproval = Topic<String, LoanApproval>( "loan-approval", StringSerde{}, AvroSerde{})
```
This defines a topic named "loan-approval" whose key will be serialized using the string serde, and whose value will be serialized from avro into the `LoanApproval` data object (which was generated with the avro plugin).

## Using `Topic`s with the Kafka Streams APIs
Kotlin [extension functions](https://kotlinlang.org/docs/reference/extensions.html) provide a lightweight mechanism for  [integrating](src/main/kotlin/kostr/streams/StreamsBuilder.kt) `Topic` definitions to the underlying java kafka streams API without re-writing or wrapping the entire thing. Defining a topology then looks like [this](src/main/kotlin/com/acmeinc/LoanApprovalTopology.kt):

```kotlin
fun loanApprovalTopology(builder: StreamsBuilder) {
    builder.stream(Topics.LoanApplication)
        .mapValues(::approveLoan)
        .to(Topics.LoanApproval)
}
```

## Testing
A bit of wrappping around `TopologyTestDriver` so tests can be written concisely:
```kotlin
@Test
fun `loan approval topology`() {
    testTopology(::loanApprovalTopology).use { topology ->
        val application = LoanApplication.generate()
        topology.produce(Topics.LoanApplication, application)
        val approval = topology.consume(Topics.LoanApproval)
        println("Result: $approval")
    }
}
```