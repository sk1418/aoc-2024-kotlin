import Direction.Right
import java.util.*

// https://adventofcode.com/2024/day/16
fun main() {
    val today = "Day16"

    val input = readInput(today)
    val testInput = readTestInput(today)
    val testAnotherInput = readTestInput("$today-another")

    fun toMatrix(input: List<String>): MatrixDay16 = buildMap {
        input.forEachIndexed { y, line -> line.forEachIndexed { x, c -> put(x to y, c) } }
    }.let { points -> MatrixDay16(input[0].lastIndex, input.lastIndex, NotNullMap(points)) }


    fun part1(input: List<String>) = toMatrix(input).solve()

    fun part2(input: List<String>) = toMatrix(input).solve(Part2)

    chkTestInput(Part1, testInput, 7036) { part1(it) }
    chkTestInput(Part1, testAnotherInput, 11048) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 45) { part2(it) }
    chkTestInput(Part2, testAnotherInput, 64) { part2(it) }
    solve(Part2, input) { part2(it) }
}


class MatrixDay16(maxX: Int, maxY: Int, override val points: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(maxX, maxY, points) {
    private val endPos = findOneByValue('E')
    private val startPos = findOneByValue('S')

    private data class PointScoreWithPath(val pos: Pair<Int, Int>, val direction: Direction, val score: Int, val path: List<Pair<Pair<Int, Int>, Direction>>)

    fun solve(part: String = Part1): Int {
        val unvisited = PriorityQueue<PointScoreWithPath> { p1, p2 -> p1.score.compareTo(p2.score) }
        val visitedPosAndDir = mutableSetOf<Pair<Pair<Int, Int>, Direction>>()
        val paths = mutableSetOf<List<Pair<Pair<Int, Int>, Direction>>>()
        unvisited.add(PointScoreWithPath(startPos, Right, 0, emptyList()))
        var minScore = -1
        while (unvisited.isNotEmpty()) {
            val (pos, direction, score, path) = unvisited.remove().also { visitedPosAndDir.add(it.pos to it.direction) }
            if (pos == endPos) {
                if (part == Part1) return score // <- part1 gets solved here
                if (minScore < 0) minScore = score
                if (score == minScore) paths.add(path) else break //<- break: all later comings are worse (larger) due to the PrioQ
            }
            unvisited.addAll(
                listOf(
                    (pos to direction.turn90()).let { (p, d) -> PointScoreWithPath(p, d, score + 1000, path + (p to d)) },
                    (pos to direction.turn90Back()).let { (p, d) -> PointScoreWithPath(p, d, score + 1000, path + (p to d)) },
                    (pos.move(direction) to direction).let { (p, d) -> PointScoreWithPath(p, d, score + 1, path + (p to d)) }
                ).filterNot { it.pos to it.direction in visitedPosAndDir || points[pos] == '#' })
        }
        return paths.flatMap { it.map { it.first } }.distinct().size // <- part2
    }
}
