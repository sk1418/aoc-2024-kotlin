// https://adventofcode.com/2024/day/13
fun main() {
    val today = "Day13"

    val input = readInput(today)
    val testInput = readTestInput(today)

    val reXY = """X.(\d+).*?Y.(\d+)""".toRegex()
    fun parse(input: List<String>) = input.filter { it.isNotBlank() }
        .chunked(3).map { iii -> iii.map { reXY.find(it).let { it!!.groupValues.let { it[1].toInt() to it[2].toInt() } } } }

    fun part1(input: List<String>) = parse(input).sumOf { play(it[0], it[1], it[2]) }

    fun part2(input: List<String>) = parse(input).sumOf { play(it[0], it[1], it[2], 10000000000000L) }

    chkTestInput(Part1, testInput, 480L) { part1(it) }
    solve(Part1, input) { part1(it) }

//Not required:    chkTestInput(Part2, testInput, 0L) { part2(it) }
    solve(Part2, input) { part2(it) }
}

fun play(a: Pair<Int, Int>, b: Pair<Int, Int>, target: Pair<Int, Int>, addition: Long = 0): Long {
    val (ax, ay) = a
    val (bx, by) = b
    val prizeX = target.first + addition
    val prizeY = target.second + addition
    // ca * ax + cb * bx = prizeX
    // ca * ay + cb * by = prizeY
    val cb = (ax * prizeY - ay * prizeX) / (ax * by - ay * bx)
    val ca = (prizeY - by * cb) / ay
    val ok = ca * ax + bx * cb == prizeX
    return if (ok) ca * 3 + cb else 0
}
