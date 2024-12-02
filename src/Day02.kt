// https://adventofcode.com/2024/day/2
fun main() {
    val today = "Day02"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun part1(input: List<String>): Int = input.count { it.toInts().isSafe() }

    //brutal force!
    fun part2(input: List<String>): Int = input.count {
        it.toInts().let { ints ->
            ints.indices.any { idx ->
                ints.toMutableList().apply { removeAt(idx) }.isSafe()
            }
        }
    }

    chkTestInput(part1(testInput), 2, Part1)
    println("[Part1]: ${part1(input)}")

    chkTestInput(part2(testInput), 4, Part2)
    println("[Part2]: ${part2(input)}")
}

private fun List<Int>.isSafe() = windowed(2).map { (a, b) -> a - b }.let {
    it.all { it in 1..3 } || it.all { it in -3..-1 }
}
