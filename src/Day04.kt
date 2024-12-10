// https://adventofcode.com/2024/day/4
fun main() {
    val today = "Day04"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun toMatrix(input: List<String>): MatrixDay04 {
        val points = buildMap {
            input.forEachIndexed { y, line ->
                line.forEachIndexed { x, c -> put(x to y, c) }
            }
        }
        return MatrixDay04(input[0].lastIndex, input.lastIndex, NotNullMap(points))
    }

    fun part1(input: List<String>): Int = toMatrix(input).countXMAS

    fun part2(input: List<String>): Int = toMatrix(input).countMasInX

    chkTestInput(Part1, testInput, 18) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 9) { part2(it) }
    solve(Part2, input) { part2(it) }
}

class MatrixDay04(maxX: Int, maxY: Int, override val points: NotNullMap<Pair<Int, Int>, Char>) : Matrix<Char>(maxX, maxY, points) {
    private val allX = findByValue('X')
    private val allA = findByValue('A')

    val countXMAS = allX.sumOf { x ->
        listOf(x.lWord(), x.rWord(), x.uWord(), x.dWord(), x.luWord(), x.ldWord(), x.ruWord(), x.rdWord()).count { it == "XMAS" }
    }
    val countMasInX = allA.count { it.masInXBackward() && it.masInXForward() }

    private fun Pair<Int, Int>.lWord() = (first - 3..first).reversed().filter { it in 0..maxX }.map { points[it to second] }.joinChars()
    private fun Pair<Int, Int>.rWord() = (first..first + 3).filter { it in 0..maxX }.map { points[it to second] }.joinChars()
    private fun Pair<Int, Int>.uWord() = (second - 3..second).reversed().filter { it in 0..maxY }.map { points[first to it] }.joinChars()
    private fun Pair<Int, Int>.dWord() = (second..second + 3).filter { it in 0..maxY }.map { points[first to it] }.joinChars()

    private fun Pair<Int, Int>.luWord() = (first - 3..first).reversed().zip((second - 3..second).reversed()).filter { p -> p.validPoint() }.map { points[it] }.joinChars()
    private fun Pair<Int, Int>.ldWord() = (first - 3..first).reversed().zip(second..second + 3).filter { p -> p.validPoint() }.map { points[it] }.joinChars()
    private fun Pair<Int, Int>.ruWord() = (first..first + 3).zip((second - 3..second).reversed()).filter { p -> p.validPoint() }.map { points[it] }.joinChars()
    private fun Pair<Int, Int>.rdWord() = (first..first + 3).zip(second..second + 3).filter { p -> p.validPoint() }.map { points[it] }.joinChars()

    private fun Pair<Int, Int>.masInXForward() = setOf(first - 1 to second - 1, first + 1 to second + 1).filter { p -> p.validPoint() }.map { points[it] }.toSet() == setOf('M', 'S')
    private fun Pair<Int, Int>.masInXBackward() = setOf(first + 1 to second - 1, first - 1 to second + 1).filter { p -> p.validPoint() }.map { points[it] }.toSet() == setOf('M', 'S')

}
