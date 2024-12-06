import Direction.*
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

const val Part1 = "Part 1"
const val Part2 = "Part 2"

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/inputs", "$name.txt").readLines()
fun readTestInput(name: String) = File("src/inputs", "$name-test.txt").readLines()

fun readInputAsInts(name: String) = File("src/inputs", "$name.txt").readLines().map { it.toInt() }

fun String.toInts(sep: String = " ") = split(sep).map { it.toInt() }
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun chkTestInput(actual: Number, expect: Number, part: String) {
    print("[TEST::$part]: $actual ").also {
        println(if (actual == expect) "✅" else "❌ Should be:$expect")
        assert(actual == expect) { "\uD83D\uDE21\uD83D\uDE21\uD83D\uDE21 Oops... " }
    }
}

fun chkTestInput(actual: String, expect: String, part: String) {
    print("[TEST::$part]: $actual ").also {
        println(if (actual == expect) "✅" else "❌ Should be:$expect")
        assert(actual == expect) { "\uD83D\uDE21\uD83D\uDE21\uD83D\uDE21 Oops... " }
    }
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}

class MutableNotNullMap<K, V>(private val map: MutableMap<K, V>) : MutableMap<K, V> by map {
    override operator fun get(key: K): V {
        return checkNotNull(map[key]) { "Key ($key) not found in the NeverNullMap" }
    }
}

class NotNullMap<K, V>(private val map: Map<K, V>) : Map<K, V> by map {
    override operator fun get(key: K): V {
        return checkNotNull(map[key]) { "Key ($key) not found in the NeverNullMap" }
    }
}

fun <T : Any?> T.alsoLog() = this.also { println(it) }
fun List<Char>.joinChars() = joinToString(separator = "") { "$it" }
open class Matrix<T : Any>(val maxX: Int, val maxY: Int, open val points: Map<Pair<Int, Int>, T>) {

    protected infix fun singleWithValue(value: T) = points.entries.first { it.value == value }
    protected infix fun withValue(value: T) = points.filterValues { it == value }

    protected fun Pair<Int, Int>.validPoint() = first in 0..maxX && second in 0..maxY
    protected fun Pair<Int, Int>.move(direction: Direction) = when (direction) {
        Up -> first to (second - 1)
        Down -> first to (second + 1)
        Left -> (first - 1) to second
        Right -> (first + 1) to second
    }

    protected fun Pair<Int, Int>.safeMove(direction: Direction) = when (direction) {
        Up -> (first to ((second - 1).takeIf { it >= 0 } ?: 0))
        Down -> (first to ((second + 1).takeIf { it <= maxY } ?: maxY))
        Left -> (((first - 1).takeIf { it >= 0 } ?: 0) to second)
        Right -> (((first + 1).takeIf { it <= maxX } ?: maxX) to second)
    }

}

enum class Direction {
    Left, Up, Right, Down;

    fun turn90() = ordinal.let { entries[if (it == 3) 0 else it + 1] }
    fun turn90Back() = ordinal.let { entries[if (it == 0) 3 else it - 1] }
}
