import Direction.*

// https://adventofcode.com/2024/day/10
fun main() {
    val today = "Day10"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toMatrix(input: List<String>) = buildMap {
        input.forEachIndexed { y, line -> line.forEachIndexed { x, c -> put(x to y, c.digitToInt()) } }
    }.let { points -> MatrixDay10(input[0].lastIndex, input.lastIndex, NotNullMap(points)) }

    fun part1(input: List<String>) = toMatrix(input).findAllPaths0To9().distinctBy { it.first() to it.last() }.size

    fun part2(input: List<String>) = toMatrix(input).findAllPaths0To9().size

    chkTestInput(Part1, testInput, 36) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 81) { part2(it) }
    solve(Part2, input) { part2(it) }
}

class MatrixDay10(maxX: Int, maxY: Int, override val points: NotNullMap<Pair<Int, Int>, Int>) : Matrix<Int>(maxX, maxY, points) {

    fun findAllPaths0To9(): MutableList<List<Pair<Int, Int>>> {
        val result: MutableList<List<Pair<Int, Int>>> = mutableListOf()
        findByValue(0).forEach { findPath(it, listOf(it), result) }
        return result
    }

    private fun findPath(pos: Pair<Int, Int>, pathSoFar: List<Pair<Int, Int>>, result: MutableList<List<Pair<Int, Int>>>) {
        val targetValue = points[pos] + 1
        pos.run { listOf(move(Left), move(Up), move(Right), move(Down)) }.filter { it.validPoint() && points[it] == targetValue }
            .forEach { aPos ->
                val newList = pathSoFar + aPos
                if (targetValue == 9) result.add(newList) else findPath(aPos, newList, result)
            }
    }
}
