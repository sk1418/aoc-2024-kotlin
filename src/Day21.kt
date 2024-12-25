import kotlin.math.absoluteValue
import kotlin.math.sign

// https://adventofcode.com/2024/day/21
fun main() {
    val today = "Day21"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun part1(input: List<String>): Long {
        return solve(input, 2)
    }

    fun part2(input: List<String>): Long {
        return solve(input, 25)
    }

    chkTestInput(Part1, testInput, 126384L) { part1(it) }
    solve(Part1, input) { part1(it) }

//    chkTestInput(Part2, testInput, 0L) { part2(it) }
    solve(Part2, input) { part2(it) }


}

operator fun Pair<Int,Int>.plus(other: Pair<Int,Int>) = Pair(first + other.first, second + other.second)
operator fun Pair<Int,Int>.minus(other: Pair<Int,Int>) = Pair(first - other.first, second - other.second)
operator fun Pair<Int,Int>.times(multiplier: Int) = Pair(first * multiplier, second * multiplier)


private fun solve(input: List<String>, robots: Int): Long = input.sumOf { complexity(it, robots) }

private fun complexity(keys: String, robots: Int): Long {
    val cache = mutableMapOf<Pair<String, Int>, Long>()
    fun simulate(aa: Char, bb: Char, level: Int): Long {
        if (aa == bb) return 1
        val key = "$aa$bb" to level
        if (key in cache) return cache.getValue(key)
        val keypad = if (level == 0) NUMERIC else ROBOTIC
        val a = keypad.getValue(aa)
        val b = keypad.getValue(bb)
        val invalid = keypad.getValue('.')
        val options = mutableListOf<List<Char>>()
        val (dx, dy) = b - a
        if ((a.first + dx to a.second) != invalid) options += buildList<Char> {
            add('A')
            for (i in 1..dx.absoluteValue) if (dx.sign < 0) add('<') else add('>')
            for (i in 1..dy.absoluteValue) if (dy.sign < 0) add('^') else add('v')
            add('A')
        }
        if ((a.first to a.second + dy) != invalid) options += buildList<Char> {
            add('A')
            for (i in 1..dy.absoluteValue) if (dy.sign < 0) add('^') else add('v')
            for (i in 1..dx.absoluteValue) if (dx.sign < 0) add('<') else add('>')
            add('A')
        }
        val size = options.minOf { option ->
            if (level == robots) option.size.toLong() - 1
            else option.zipWithNext().sumOf { (a, b) -> simulate(a, b, level + 1) }
        }
        return size.also { cache[key] = it }
    }

    val lowest = "A$keys".zipWithNext().sumOf { (a, b) -> simulate(a, b, 0) }
    val multiplier = keys.substringBeforeLast('A').toLong()
    return lowest * multiplier
}

private val NUMERIC = mapOf(
    '7' to Pair(0, 0), '8' to Pair(1, 0), '9' to Pair(2, 0),
    '4' to Pair(0, 1), '5' to Pair(1, 1), '6' to Pair(2, 1),
    '1' to Pair(0, 2), '2' to Pair(1, 2), '3' to Pair(2, 2),
    '.' to Pair(0, 3), '0' to Pair(1, 3), 'A' to Pair(2, 3),
)

private val ROBOTIC = mapOf(
    '.' to Pair(0, 0), '^' to Pair(1, 0), 'A' to Pair(2, 0),
    '<' to Pair(0, 1), 'v' to Pair(1, 1), '>' to Pair(2, 1),
)