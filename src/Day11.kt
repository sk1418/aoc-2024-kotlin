import kotlin.math.log10
import kotlin.math.pow

// https://adventofcode.com/2024/day/11
fun main() {
    val today = "Day11"

    val input = readInput(today)
    val testInput = readTestInput(today)

    val dict: MutableMap<Pair<Long, Int>, Long> = mutableMapOf()


    fun countNumbers(n: Long, times: Int): Long = if (times == 0) 1 else dict.getOrPut(n to times) {
        if (n == 0L) listOf(1L) else {
            val lg = log10(n.toDouble()).toInt()
            if (lg % 2 == 1) {
                (10.0.pow((lg / 2) + 1)).toInt().let { pow -> listOf((n % pow), (n / pow)) }
            } else listOf(2024L * n)
        }.sumOf { countNumbers(it, times - 1) }
    }

    fun part1(input: List<String>) = input.first().toLongs().sumOf { n -> countNumbers(n, 25) }

    fun part2(input: List<String>) = input.first().toLongs().sumOf { n -> countNumbers(n, 75) }

    chkTestInput(Part1, testInput, 55312L) { part1(it) }
    solve(Part1, input) { part1(it) }

// No Test for part2:   chkTestInput(Part2, testInput, 189547L) { part2(it) }
    solve(Part2, input) { part2(it) }
}
