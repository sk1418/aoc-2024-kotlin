// https://adventofcode.com/2024/day/19
fun main() {
    val today = "Day19"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun parseInput(input: List<String>): Pair<List<String>, List<String>> = input[0].split(", ").toList() to input.drop(2)

    fun part1(input: List<String>): Int {
        val (patterns, designs) = parseInput(input)
        val re = patterns.joinToString(prefix = "^(", postfix = ")+$", separator = "|").toRegex()
        return designs.count {re in it}
    }

    val cache: MutableMap<String, Long> = mutableMapOf()
    fun allPossibleCount(patterns: List<String>, design: String): Long {
        return cache.getOrPut(design) {
            if (design.isEmpty()) 1 else
                patterns.filter { design.startsWith(it) }.sumOf { p -> allPossibleCount(patterns, design.drop(p.length)) }
        }
    }

    fun part2(input: List<String>): Long {
        val (patterns, designs) = parseInput(input)
        return designs.sumOf { cache.clear(); allPossibleCount(patterns, it) }
    }


    chkTestInput(Part1, testInput, 6) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 16L) { part2(it) }
    solve(Part2, input) { part2(it) }
}
