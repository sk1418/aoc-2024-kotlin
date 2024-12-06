// https://adventofcode.com/2024/day/6
import Direction.Up

fun main() {
    val today = "Day06"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toMatrix(input: List<String>): MatrixDay06 {
        val points = buildMap {
            input.forEachIndexed { y, line ->
                line.forEachIndexed { x, c -> put(x to y, c) }
            }
        }
        return MatrixDay06(input[0].lastIndex, input.lastIndex, NotNullMap(points))
    }

    fun part1(input: List<String>): Int = toMatrix(input).patrol()!!.map { it.first }.distinct().size

    fun part2(input: List<String>): Int {
        val matrix = toMatrix(input)
        val visited = matrix.patrol()!!.map { it.first }.distinct()
        return (visited - (matrix.start)).count {
            matrix.newMatrixWithNewObstruction(it).patrol() == null
        }
    }

    chkTestInput(part1(testInput), 41, Part1)
    println("[Part1]: ${part1(input)}")

    chkTestInput(part2(testInput), 6, Part2)
    println("[Part2]: ${part2(input)}")
}

class MatrixDay06(maxX: Int, maxY: Int, override val points: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(maxX, maxY, points) {
    val start = singleWithValue('^').key

    fun newMatrixWithNewObstruction(pos: Pair<Int, Int>): MatrixDay06 =
        MatrixDay06(maxX, maxY, NotNullMap(points.toMutableMap().also { it[pos] = '#' }))

    fun patrol(): MutableSet<Pair<Pair<Int, Int>, Direction>>? {
        val visited = mutableSetOf<Pair<Pair<Int, Int>, Direction>>()
        var direction = Up
        var cur = start
        while (true) {
            visited.add(cur to direction)
            val next = cur.move(direction)
            if (next in points) {
                if (points[next] == '#')
                    direction = direction.turn90()
                else {
                    cur = next
                    if (cur to direction in visited) return null
                }
            } else
                break
        }
        return visited
    }
}
