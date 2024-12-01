import kotlin.math.absoluteValue

// https://adventofcode.com/2024/day/1
fun main() {
    val today = "Day01"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun part1(input: List<String>): Int {
        val leftList = input.map { it.substringBefore(" ").toInt() }.sorted()
        val rightList = input.map { it.substringAfterLast(" ").toInt() }.sorted()
        return leftList.zip(rightList) { l, r -> (l - r).absoluteValue }.sum()
    }

    fun part2(input: List<String>): Int {
        val leftList = input.map { it.substringBefore(" ").toInt() }
        val rightMap = input.map { it.substringAfterLast(" ").toInt() }.groupingBy { it }.eachCount()
        return leftList.sumOf { it * rightMap.getOrDefault(it, 0) }
    }

    chkTestInput(part1(testInput), 11, Part1)
    println("[Part1]: ${part1(input)}")

    chkTestInput(part2(testInput), 31, Part2)
    println("[Part2]: ${part2(input)}")
}
