// https://adventofcode.com/2024/day/3
fun main() {
    val today = "Day03"

    val input = readInput(today)
    val testInput = readTestInput(today)
    val testInputPart2 = readTestInput("$today-part2")

    val mulRe = """mul[(](\d{1,3},\d{1,3})[)]""".toRegex()
    val disabledRe = """don't[(][)].*?(do[(][)]|$)""".toRegex()

    fun mul(csvPair: String) = csvPair.split(",").let { it[0].toLong() * it[1].toLong() }

    fun part1(input: List<String>): Long {
        return input.sumOf { line -> mulRe.findAll(line).map { mul(it.groupValues[1]) }.sum() }
    }

    fun part2(input: List<String>): Long {
        val enabledLine = input.joinToString(separator = "").replace(disabledRe, "")
        return mulRe.findAll(enabledLine).map { mul(it.groupValues[1]) }.sum()
    }

    chkTestInput(Part1, testInput, 161L) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInputPart2, 48L) { part2(it) }
    solve(Part2, input) { part2(it) }
}
