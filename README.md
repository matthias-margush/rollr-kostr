# rollr-kostr

![rollr-kostr](rollercoaster-297308_640.png)
<!-- (Image from https://pixabay.com/vectors/rollercoaster-roller-coaster-297308/) -->

This project implements a few small experiments related to using kafka streams with kotlin.

## Setup
```bash
# Tools
brew install gradle

# Bootstrap
gradle wrapper --gradle-version 5.4.1

# Build 
gradle test # --info for more details

# Intellij
idea .
```
## Through the Corkscrew

### 🎢 Pass-through Data
If a topology wants to carry along and propagate unknown fields through a topology (for forward-compatibility), a [map-backed data class](src/main/kotlin/com/acmeinc/LoanApplication.kt) is suitable.

```kotlin
data class LoanApplication(override val map: Map<String, Any?>) {
    val id: UUID by map
    val firstName: String by map
    val lastName: String by map
    val income: Long by map
}
```

### 🎢 Avro
- In this experiment a Java data class is generated from an [avro spec](src/main/avro/loan-approval.avsc) as part of `gradle build` using the [gradle-avro-plugin](https://github.com/commercehub-oss/gradle-avro-plugin). (A generated Kotlin data class would be nicer, but using a Java class is seamless from Kotlin.)

### 🎢 EDN
- (I wouldn't suggest making EDN a primary format when working w/Kotlin, but it's not hard if needed.)
- Leverages [edn-java](https://github.com/bpsm/edn-java)
- [Basic kafka serde](src/main/kotlin/kostr/serde/Edn.kt) handles maps, rejects any other kind of EDN string (ready to be enhanced further as needed)
- Implementing `kotlinx.serialization` would be nice.

### 🎢 Topics
Topics & serdes go together conceptually, but the Java kafka APIs don't bundle topics & serdes in a data structure. Tracking those all separately can be a bit typo-prone if there are more than a few topics. The project simplifies this by defining a `Topic` type that packages up [topics](src/main/kotlin/kostr/Topic.kt) and [serdes](src/main/kotlin/kostr/serde/Serde.kt) together. Define a topic like this:

```kotlin
val LoanApprovalTopic = Topic<String, LoanApproval>( "loan-approval", StringSerde{}, AvroSerde{})
```
This defines a topic named "loan-approval" whose key will be serialized using the string serde, and whose value will be serialized from avro into the `LoanApproval` data object (which was generated with the avro plugin).

### 🎢 Topologies
Kotlin [extension functions](https://kotlinlang.org/docs/reference/extensions.html) provide a lightweight mechanism for  [integrating](src/main/kotlin/kostr/streams/StreamsBuilder.kt) `Topic` definitions with the underlying java kafka streams API without re-writing or wrapping the entire thing. Defining a topology then looks like [this](src/main/kotlin/com/acmeinc/LoanApprovalTopology.kt):

```kotlin
fun loanApprovalTopology(builder: StreamsBuilder) =
    builder.stream(Topics.LoanApplication)
        .mapValues(::approveLoan) to Topics.LoanApproval
```

### 🎢 Tests
A bit of wrappping around `TopologyTestDriver` so tests can be written concisely:
```kotlin
@Test
fun `loan approval topology`() =
    mocktop(::loanApprovalTopology).use { topology ->
        val application = LoanApplication.generate()
        topology.produce(Topics.LoanApplication, application)
        val approval = topology.consume(Topics.LoanApproval)
        println("Result: $approval")
    }
```