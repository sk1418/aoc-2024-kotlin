// https://adventofcode.com/2024/day/25
fun main() {
    val today = "Day25"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toMatrix(input: List<String>): MatrixDay25 = buildMap {
        input.forEachIndexed { y, line -> line.forEachIndexed { x, c -> if (c == '#') put(x to y, c) } }
    }.let { points -> MatrixDay25(points.toNotNullMap()) }

    fun parseInput(input: List<String>): Pair<MutableList<MatrixDay25>, MutableList<MatrixDay25>> {
        val (locks, keys) = mutableListOf<MatrixDay25>() to mutableListOf<MatrixDay25>()
        var theInput = input
        while (theInput.isNotEmpty()) {
            theInput.takeWhile { it.isNotBlank() }.let { chunk ->
                toMatrix(chunk).let { if ('#' in chunk.first()) locks.add(it) else keys.add(it) }
                theInput = theInput.drop(chunk.size + 1)
            }
        }
        return locks to keys
    }

    fun part1(input: List<String>): Int {
        val (theLocks, theKeys) = parseInput(input)
        return theKeys.map { it.points.keys }.sumOf { keyPoints ->
            theLocks.map { it.points.keys }.count { lockPoints ->
                lockPoints.intersect(keyPoints).isEmpty()
            }
        }
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    chkTestInput(Part1, testInput, 3) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 0L) { part2(it) }
    solve(Part2, input) { part2(it) }
}


class MatrixDay25(override val points: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(4, 6, points) {

}