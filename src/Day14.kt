// https://adventofcode.com/2024/day/14
fun main() {
    val today = "Day14"

    val input = readInput(today)
    val testInput = readTestInput(today)

    val robotRE = """(\d+),(\d+).*?(-?\d+),(-?\d+)""".toRegex()
    fun toMatrix(maxX: Int, maxY: Int, input: List<String>) = MatrixDay14(
        maxX, maxY, robots = input.map { line ->
            robotRE.find(line)!!.groupValues.let { g ->
                Robot(g[1].toInt() to g[2].toInt(), g[3].toInt(), g[4].toInt())
            }
        })

    fun part1(width: Int, height: Int, input: List<String>) = toMatrix(width - 1, height - 1, input).calcAfterMoving(100)

    fun part2(width: Int, height: Int, input: List<String>) = toMatrix(width - 1, height - 1, input).formingTreeNeeds()

    chkTestInput(Part1, testInput, 12) { part1(11, 7, it) }
    solve(Part1, input) { part1(101, 103, it) }

//No test for part2    chkTestInput(Part2, testInput, 0L) { part2(11, 7, it) }
    solve(Part2, input) { part2(101, 103, it) }
}

private data class Robot(var pos: Pair<Int, Int>, val vX: Int, val vY: Int)

private data class MatrixDay14(val maxX: Int, val maxY: Int, val robots: List<Robot>) {
    fun Robot.move(times: Int = 1) {
        val posX = calcFinalPos(pos.first, times * vX, maxX + 1)
        val posY = calcFinalPos(pos.second, times * vY, maxY + 1)
        pos = posX to posY
    }

    private fun calcFinalPos(o: Int, addition: Int, len: Int) = ((o + addition) % len).let { if (it >= 0) it else it + len }


    operator fun Pair<IntRange, IntRange>.contains(pos: Pair<Int, Int>) = pos.first in first && pos.second in second

    fun formingTreeNeeds(): Int {// all robots sit at a unique point
        var s = 0
        while (true) {
            s++
            if (robots.onEach { it.move() }.distinctBy { it.pos }.size == robots.size) break
        }
        return s
    }

    fun calcAfterMoving(times: Int): Int {
        val ulBlock = 0..<maxX / 2 to 0..<maxY / 2
        val dlBlock = 0..<maxX / 2 to maxY / 2 + 1..maxY
        val urBlock = maxX / 2 + 1..maxX to 0..<maxY / 2
        val drBlock = maxX / 2 + 1..maxX to maxY / 2 + 1..maxY
        var (ul, ur, dl, dr) = listOf(0, 0, 0, 0)
        robots.onEach { it.move(times) }.groupBy { it.pos }.entries.forEach { (pos, rList) ->
            when (pos) {
                in ulBlock -> ul += rList.size
                in urBlock -> ur += rList.size
                in dlBlock -> dl += rList.size
                in drBlock -> dr += rList.size
            }
        }
        return ul * ur * dl * dr
    }
}
