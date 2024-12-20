import Direction.*
import kotlin.math.abs

// https://adventofcode.com/2024/day/20
fun main() {
    val today = "Day20"

    val input = readInput(today)
    val testInput = readTestInput(today)
    fun toMatrix(input: List<String>): MatrixDay20 {
        val points = buildMap {
            input.forEachIndexed { y, line -> line.forEachIndexed { x, c -> put(x to y, c) } }
        }
        return MatrixDay20(input[0].lastIndex, input.lastIndex, NotNullMap(points))
    }

    fun part1(input: List<String>, atLeastSave: Int) = toMatrix(input).solve(2, atLeastSave)

    fun part2(input: List<String>, atLeastSave: Int) = toMatrix(input).solve(20, atLeastSave)

    chkTestInput(Part1, testInput, 5) { part1(it, 20) }
    solve(Part1, input) { part1(it, 100) }

    chkTestInput(Part2, testInput, 29) { part2(it, 72) }
    solve(Part2, input) { part2(it, 100) }
}

class MatrixDay20(maxX: Int, maxY: Int, override val points: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(maxX, maxY, points) {
    private val startPos = findOneByValue('S')
    private val endPos = findOneByValue('E')
    private val path = buildList {
        var cur = startPos
        add(cur)
        while (cur != endPos) {
            cur = listOf(cur.move(Up), cur.move(Down), cur.move(Left), cur.move(Right)).first { it.validPoint() && it !in this@buildList && points[it] != '#' }
            add(cur)
        }
    }

    infix fun Pair<Int, Int>.manhattanDistanceTo(other: Pair<Int, Int>) = abs(first - other.first) + abs(second - other.second)

    fun solve(cheatCost: Int, saveTime: Int): Int {
        return path.withIndex().sumOf { (idx, pos) ->
            path.slice(idx..path.lastIndex).withIndex().count { (idx2, pos2) ->
                (pos manhattanDistanceTo pos2).let { cost -> cost in 2..cheatCost && idx2 - cost >= saveTime }

            }
        }
    }

}
