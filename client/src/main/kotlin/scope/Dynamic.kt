package scope

import java.io.Closeable
import java.util.*

class Dynamic<T>() : Closeable {
    private val stack: Stack<T> = Stack<T>()

    companion object {
        fun <T> binding(defaultValue: T): Dynamic<T> {
            val d = Dynamic<T>()
            d.stack.push(defaultValue)
            return d
        }
    }

    fun withBinding(binding: T): Closeable {
        this.bind(binding)
        return this
    }


    fun bound(): T {
        return stack.peek()
    }

    fun bind(binding: T) {
        stack.push(binding)
    }

    override fun close() {
        stack.pop()
    }
}
