// https://adventofcode.com/2024/day/18
fun main() {
    val today = "Day18"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun part1(input: List<String>): Long {
        return 0
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    chkTestInput(Part1, testInput, 0L) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 0L) { part2(it) }
    solve(Part2, input) { part2(it) }
}
