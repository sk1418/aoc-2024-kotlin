// https://adventofcode.com/2024/day/22
fun main() {
    val today = "Day22"

    val input = readInput(today)
    val testInput = readTestInput(today)
    val part2TestInput = readTestInput("$today-part2")

    fun part1(input: List<String>) = input.map { it.toLong() }.sumOf { s -> generateSecrets(s).last() }

    fun part2(input: List<String>): Long {
        val result = mutableMapOf<List<Long>, Long>()
        input.map { it.toLong() }.map { s -> generateSecrets(s).toFourSeqMap() }
            .flatMap { it.entries }.forEach { (k, v) ->
                result.merge(k, v, Long::plus)
            }
        return result.values.max()
    }

    chkTestInput(Part1, testInput, 37327623L) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, part2TestInput, 23L) { part2(it) }
    solve(Part2, input) { part2(it) }
}


private fun generateSecrets(secret: Long) = generateSequence(secret) { sec ->
    val s1 = (sec shl 6).mix(sec).prune()
    val s2 = (s1 shr 5).mix(s1).prune()
    (s2 shl 11).mix(s2).prune()
}.take(2001)

private fun Long.mix(other: Long) = this xor other
private fun Long.prune() = this % 16777216

private fun Sequence<Long>.toFourSeqMap() = map { it % 10 }
    .zipWithNext { a, b -> b to b - a }
    .windowed(4) { (a, b, c, d) -> listOf(a.second, b.second, c.second, d.second) to d.first }
    .groupingBy { it.first }
    .fold({ _, value -> value.second }, { _, a, _ -> a })
