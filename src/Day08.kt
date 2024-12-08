import kotlin.math.abs
import kotlin.math.max

// https://adventofcode.com/2024/day/8
fun main() {
    val today = "Day08"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toMatrix(input: List<String>): MatrixDay08 = buildMap {
        input.forEachIndexed { y, line -> line.forEachIndexed { x, c -> put(x to y, c) } }
    }.let { points -> MatrixDay08(input[0].lastIndex, input.lastIndex, NotNullMap(points)) }

    fun part1(input: List<String>): Int = toMatrix(input).countAntiNodes(Part1)

    fun part2(input: List<String>): Int = toMatrix(input).countAntiNodes(Part2)

    chkTestInput(Part1, testInput, 14) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 34) { part2(it) }
    solve(Part2, input) { part2(it) }
}

class MatrixDay08(maxX: Int, maxY: Int, override val points: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(maxX, maxY, points) {
    fun countAntiNodes(part1or2: String) = buildSet {
        points.entries.filter { it.value != '.' }.map { it.value to it.key }.groupBy({ it.first }, { it.second }).values
            .forEach { pList ->
                pList.forEach { posA -> //antiNodes for each pos (posA)
                    (pList - setOf(posA)).forEach { posB ->
                        val (x, y) = (posA.first - posB.first) to (posA.second - posB.second)
                        val factors = if (part1or2 == Part1) 1..1 else 0..max(maxX / abs(x), maxY / abs(y))
                        factors.forEach { factor -> add(posA.first + x * factor to posA.second + y * factor) }
                    }
                }
            }
    }.count { it.validPoint() }
}
