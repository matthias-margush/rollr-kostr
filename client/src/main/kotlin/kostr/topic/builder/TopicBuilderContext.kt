package kostr.topic.builder

sealed class TopicBuilderContext
object DefaultTopicBuilderContext : TopicBuilderContext()
data class MockedTopicBuilderContext(val scope: String) : TopicBuilderContext()

