import Direction.*
import java.io.File
import java.util.*
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/inputs", "$name.txt").readLines()
fun readTestInput(name: String) = File("src/inputs", "$name-test.txt").readLines()

fun readInputAsInts(name: String) = File("src/inputs", "$name.txt").readLines().map { it.toInt() }

fun String.toInts(sep: String = " ") = split(sep.toRegex()).map { it.toInt() }
fun String.toLongs(sep: String = " ") = split(sep.toRegex()).map { it.toLong() }

fun <T : Any?> T.alsoLog(prefix: String = "${this!!::class.simpleName}", msg: (T) -> String = { t: T -> "$t" }) = apply { println("[$prefix] ${msg(this)}") }

fun <T> chkTestInput(part: String, testInput: List<String>, expected: T, solveIt: (List<String>) -> T) = measureTimedValue { solveIt(testInput) }
    .also { (actual, time) ->
        println("$CYAN[$part::: TEST ]$RESET: $actual  ${if (actual == expected) "${CYAN}✔$RESET" else "$RED✘$RESET Should be: $RED$expected$RESET  "}    ($time)")
        check(actual == expected)
    }

fun <T> solve(part: String, input: List<String>, solveIt: (List<String>) -> T) = measureTimedValue { solveIt(input) }
    .also { (result, time) -> println("$BLUE[$part:::RESULT]$RESET: $result    ($time)\n") }

// list
fun List<Char>.joinChars() = joinToString(separator = "") { "$it" }

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}

fun <T> List<T>.prepend(e: T): List<T> = buildList {
    add(e)
    addAll(this@prepend)
}

fun <T> permutations(input: List<T>): List<List<T>> {
    fun permutationsRecursive(input: List<T>, index: Int, answers: MutableList<List<T>>) {
        if (index == input.lastIndex) answers.add(input.toList())
        for (i in index..input.lastIndex) {
            Collections.swap(input, index, i)
            permutationsRecursive(input, index + 1, answers)
            Collections.swap(input, i, index)
        }
    }

    val solutions = mutableListOf<List<T>>()
    permutationsRecursive(input, 0, solutions)
    return solutions
}

//map
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

//Matrix related:
open class Matrix<T : Any>(val maxX: Int, val maxY: Int, open val points: Map<Pair<Int, Int>, T>) {

    protected fun findOneByValue(value: T) = points.entries.first { it.value == value }.key
    protected fun findByValue(value: T) = points.filterValues { it == value }.keys

    protected fun Pair<Int, Int>.validPoint() = first in 0..maxX && second in 0..maxY
    protected fun Pair<Int, Int>.invalidPoint() = validPoint().not()

    operator fun contains(pos: Pair<Int, Int>) = pos.validPoint()
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

    protected fun Pair<Int, Int>.allAround() =
        listOf(
            move(Up), move(Left), move(Down), move(Right),
            move(Up).move(Left), move(Up).move(Right), move(Down).move(Left), move(Down).move(Right)
        ).filter { it.validPoint() }

    override fun toString(): String = buildString {
        append('\n')
        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                append(if (x to y in points) points[x to y] else ' ')
            }
            append('\n')
        }
    }

}

enum class Direction {
    Left, Up, Right, Down;

    fun isHorizontal() = this == Left || this == Right
    fun isVertical() = this == Up || this == Down
    fun opposite() = ordinal.let { entries[(it + 2).let { if (it > 3) it - 4 else it }] }
    fun turn90() = ordinal.let { entries[if (it == 3) 0 else it + 1] }
    fun turn90Back() = ordinal.let { entries[if (it == 0) 3 else it - 1] }
}

const val Part1 = "Part 1"
const val Part2 = "Part 2"

const val RESET = "\u001B[0m"
const val RED = "\u001B[31m"
const val BLUE = "\u001B[34m"
const val CYAN = "\u001B[36m"
