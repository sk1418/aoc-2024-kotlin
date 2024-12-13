import Direction.*

// https://adventofcode.com/2024/day/12
fun main() {
    val today = "Day12"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toMatrix(input: List<String>) = buildMap {
        input.forEachIndexed { y, line -> line.forEachIndexed { x, c -> put(x to y, c) } }
    }.let { points -> MatrixDay12(input[0].lastIndex, input.lastIndex, NotNullMap(points)) }

    fun part1(input: List<String>): Int = toMatrix(input).perimeterBasedPrice()

    fun part2(input: List<String>): Int = toMatrix(input).sideBasedPrice()

    chkTestInput(Part1, testInput, 1930) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 1206) { part2(it) }
    solve(Part2, input) { part2(it) }
}

class MatrixDay12(maxX: Int, maxY: Int, override val points: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(maxX, maxY, points) {
    private val visited = mutableSetOf<Pair<Int, Int>>()

    fun perimeterBasedPrice(): Int {
        return points.entries.sumOf { (pos, c) ->
            val pos2Perimeters = mutableMapOf<Pair<Int, Int>, Int>()
            calcOneRegion(pos, c, pos2Perimeters)
            pos2Perimeters.let { it.values.sum() * it.size }
        }
    }

    private fun calcOneRegion(pos: Pair<Int, Int>, c: Char, pos2perimeters: MutableMap<Pair<Int, Int>, Int>) {
        if (visited.add(pos)) {
            val neighbours = pos neighboursWithValue c
            pos2perimeters[pos] = (4 - neighbours.size)
            neighbours.filter { it !in visited }.forEach { calcOneRegion(it, c, pos2perimeters) }
        }
    }

    private infix fun Pair<Int, Int>.neighboursWithValue(value: Char): List<Pair<Int, Int>> =
        listOf(move(Up), move(Left), move(Down), move(Right)).filter { it.validPoint() && value == points[it] }

    fun sideBasedPrice(): Int {
        return points.entries.sumOf { (pos, c) ->
            val pos2Perimeters = mutableMapOf<Pair<Int, Int>, Int>()
            calcOneRegion(pos, c, pos2Perimeters)
            pos2Perimeters.let { calcSides(it.keys) * it.size }
        }
    }

    private fun calcSides(posSet: Set<Pair<Int, Int>>): Int = buildList { // calc sides by "invalid" pos
        posSet.forEach { pos -> Direction.entries.forEach { dir -> if (pos.move(dir) !in posSet) add(dir to pos.move(dir)) } }
    }.groupBy { it.first }.entries.sumOf {
        when (it.key) {
            Left, Right -> outAfterHMove(it.value.map { (_, pos) -> pos })
            Up, Down -> outAfterVMove(it.value.map { (_, pos) -> pos })
        }
    }

    private fun outAfterHMove(hPos: List<Pair<Int, Int>>) = hPos.groupBy { it.first }.entries.sumOf { countDifferentSegments(it.value.map { it.second }.sorted()) }
    private fun outAfterVMove(vPos: List<Pair<Int, Int>>) = vPos.groupBy { it.second }.entries.sumOf { countDifferentSegments(it.value.map { it.first }.sorted()) }

    private fun countDifferentSegments(sortedNumbers: List<Int>) = buildList {
        (1..sortedNumbers.lastIndex).forEach { add(sortedNumbers[it] - sortedNumbers[it - 1]) }
    }.prepend("42").filter { it != 1 }.size // skip sequences like 1 2 3
}
