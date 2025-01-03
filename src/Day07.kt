// https://adventofcode.com/2024/day/7
fun main() {
    val today = "Day07"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toCalc(input: List<String>) = input.map { it.toLongs(":? ").let { it.first() to it.drop(1) } }

    infix fun Pair<Long, List<Long>>.equationStandsWith(allPossibleResults: (Long, Long) -> List<Long>): Boolean {
        val (target, numbers) = this
        if (numbers.first() > target) return false
        if (numbers.size == 2) return allPossibleResults(numbers[0], numbers[1]).any { it == target }
        return allPossibleResults(numbers[0], numbers[1]).any { preResult -> target to numbers.drop(2).prepend(preResult) equationStandsWith allPossibleResults }
    }

    fun part1(input: List<String>): Long = toCalc(input).sumOf { target2num ->
        if (target2num equationStandsWith { a, b -> listOf(a * b, a + b) }) target2num.first else 0
    }

    fun part2(input: List<String>): Long = toCalc(input).sumOf { target2num ->
        if (target2num equationStandsWith { a, b -> listOf(a * b, a + b, "$a$b".toLong()) }) target2num.first else 0
    }

    chkTestInput(Part1, testInput, 3749L) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 11387L) { part2(it) }
    solve(Part2, input) { part2(it) }
}
