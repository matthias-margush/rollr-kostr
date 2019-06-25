package kostr.serde

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EdnTest {
    @Test
    fun `An empty payload should throw an exception`() {
        assertFailsWith<EDNFormatException> {
            parseMap("")
        }
    }

    @Test
    fun `Multiple top level forms should throw an exception`() {
        assertFailsWith<EDNFormatException> {
            parseMap("{}{}")
        }
    }

    @Test
    fun `A non-map top-level form should throw an exception`() {
        assertFailsWith<EDNFormatException> {
            parseMap("[]")
        }
        assertFailsWith<EDNFormatException> {
            parseMap("()")
        }
        assertFailsWith<EDNFormatException> {
            parseMap("1")
        }
        assertFailsWith<EDNFormatException> {
            parseMap("null")
        }
    }

    @Test
    fun `A nested EDN structure should throw an exception`() {
        assertFailsWith<EDNFormatException> {
            parseMap("{:a {} :b [] :c #{} :d ()}")
        }
    }

    @Test
    fun `De-namespaced keys that conflict should throw an exception`() {
        assertFailsWith<EDNFormatException> {
            parseMap("{:foo/bar 2 :baz/bar 1}")
        }
    }

    @Test
    fun `An EDN map should deserialize successfully`() {
        assertEquals(
            mapOf(
                "fooBar" to 1L,
                "batQuz" to 2L,
                "a" to "str",
                "b" to 4L
            ),
            parseMap("{:foo-bar 1 :baz/bat-quz 2 :a \"str\", :b #foo 4}")
        )
    }
}
