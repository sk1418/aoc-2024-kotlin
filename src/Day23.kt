// https://adventofcode.com/2024/day/23
fun main() {
    val today = "Day23"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toPairs(input: List<String>) = input.map { it.split('-').toSet() }.toSet()
    fun toDict(input: List<String>) = NotNullMap(input.flatMap { it.split('-').let { (one, two) -> listOf(one to two, two to one) } }
        .groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() })

    fun part1(input: List<String>): Int {
        val dict = toDict(input)
        return toPairs(input).moreConnectedNodes(dict).count { it.any { it.startsWith("t") } }
    }

    fun part2(input: List<String>): String {
        val dict = toDict(input)
        var result = toPairs(input).moreConnectedNodes(dict)
        while (result.size > 1) {
            result = result.moreConnectedNodes(dict)
        }
        return result.first().sorted().joinToString(",")
    }

    chkTestInput(Part1, testInput, 7) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, "co,de,ka,ta") { part2(it) }
    solve(Part2, input) { part2(it) }

}

private fun Set<String>.connected(dict: NotNullMap<String, Set<String>>) =
    map { dict[it] }.reduce(Set<String>::intersect)

private fun Set<Set<String>>.moreConnectedNodes(dict: NotNullMap<String, Set<String>>): Set<Set<String>> {
    return flatMap { set1 ->
        set1.connected(dict).map { node -> set1 + node }
    }.toSet()
}
