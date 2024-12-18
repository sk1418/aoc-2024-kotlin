import Direction.Right
import java.util.*

// https://adventofcode.com/2024/day/18
fun main() {
    val today = "Day18"

    val input = readInput(today).prepend("70")
    val testInput = readTestInput(today).prepend("6")

    fun toMatrix(input: List<String>): MatrixDay18 {
        val range = 0..input.first().toInt()
        val allBytes = LinkedHashMap<Pair<Int, Int>, Char>().apply {
            input.drop(1).forEach { val xy = it.toInts(","); put(xy[0] to xy[1], '#') }
        }
        return buildMap {
            range.forEach { x -> range.forEach { y -> put(x to y, '.') } }
        }.toMutableMap().let { points ->
            MatrixDay18(range.last, range.last, MutableNotNullMap(points), allBytes = NotNullMap(allBytes))
        }
    }

    fun part1(input: List<String>, firstFalling: Int) = toMatrix(input).findPathWithFallenBytes(firstFalling)

    fun part2(input: List<String>): Pair<Int, Int> = toMatrix(input).findTheCutPoint()

    chkTestInput(Part1, testInput, 22) { part1(it, 12) }
    solve(Part1, input) { part1(it, 1024) }

    chkTestInput(Part2, testInput, 6 to 1) { part2(it) }
    solve(Part2, input) { part2(it) }
}

class MatrixDay18(maxX: Int, maxY: Int, override val points: MutableNotNullMap<Pair<Int, Int>, Char>, val allBytes: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(maxX, maxY, points) {
    private val endPos = maxX to maxY
    private val startPos = 0 to 0

    private fun resetMatrixByFallenBytes(fallenBytes: Int): MatrixDay18 = apply {
        points.keys.forEach { points[it] = '.' }
        allBytes.keys.take(fallenBytes).forEach { points[it] = '#' }
    }

    fun findPathWithFallenBytes(fallenBytes: Int): Int {
        return resetMatrixByFallenBytes(fallenBytes).findPath()
    }

    private data class PointScore(val pos: Pair<Int, Int>, val direction: Direction, val cost: Int)

    private fun findPath(): Int {
        val unvisited = PriorityQueue<PointScore> { p1, p2 -> p1.cost.compareTo(p2.cost) }
        val visitedPosAndDir = mutableSetOf<Pair<Pair<Int, Int>, Direction>>()
        unvisited.add(PointScore(startPos, Right, 0))
        while (unvisited.isNotEmpty()) {
            val (pos, direction, cost) = unvisited.remove().also { visitedPosAndDir.add(it.pos to it.direction) }
            if (pos == endPos) return cost
            unvisited.addAll(
                listOf(
                    (pos to direction.turn90()).let { (p, d) -> PointScore(p, d, cost) },
                    (pos to direction.turn90Back()).let { (p, d) -> PointScore(p, d, cost) },
                    (pos.move(direction) to direction).let { (p, d) -> PointScore(p, d, cost + 1) }
                ).filterNot { it.pos.invalidPoint() || it.pos to it.direction in visitedPosAndDir || points[pos] == '#' })
        }
        return -1 //no path found
    }

    fun findTheCutPoint(): Pair<Int, Int> = allBytes.keys.toList()[binSearchOffset()]

    private fun binSearchOffset(): Int {
        val rear = allBytes.size - 1
        var prePassOffset = 0
        var preNoPassOffset = rear
        var offset = rear
        while (preNoPassOffset - prePassOffset != 1) {
            resetMatrixByFallenBytes(offset)
            if (findPath() == -1) {
                preNoPassOffset = offset
                offset = prePassOffset + (offset - prePassOffset) / 2
            } else {
                prePassOffset = offset
                offset += (rear - offset) / 2
            }
        }
        return preNoPassOffset - 1
    }

}
