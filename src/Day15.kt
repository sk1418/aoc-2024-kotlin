import Direction.*

// https://adventofcode.com/2024/day/15
fun main() {
    val today = "Day15"

    val input = readInput(today)
    val testInput = readTestInput(today)
    val smallTestInput = readTestInput("$today-small")

    val moveSeq = { inputLines: List<String> ->
        inputLines.takeLastWhile { it.isNotBlank() }.joinToString("").toList().map { c ->
            when (c) {
                '>' -> Right
                '^' -> Up
                'v' -> Down
                '<' -> Left
                else -> error("won't happen")
            }
        }
    }

    fun toMatrix(input: List<String>): MatrixDay15 {
        var maxX = 0
        var maxY = 0
        val points = buildMap {
            input.takeWhile { it.isNotBlank() } // excluding borders (walls)
                .let { it.slice(1..<it.lastIndex) }.also { maxY = it.lastIndex }
                .map { it.slice(1..<it.lastIndex) }.also { maxX = it[0].lastIndex }
                .forEachIndexed { y, line -> line.forEachIndexed { x, c -> put(x to y, c) } }
        }.toMutableMap()
        return MatrixDay15(maxX, maxY, MutableNotNullMap(points), moveSeq(input))
    }

    fun toMatrixPart2(input: List<String>): MatrixDay15 {
        var maxX = 0
        var maxY = 0
        val points = buildMap {
            input.takeWhile { it.isNotBlank() } // excluding borders (walls)
                .let { it.slice(1..<it.lastIndex) }.also { maxY = it.lastIndex }
                .map { it.slice(1..<it.lastIndex) }.also { maxX = it[0].lastIndex * 2 + 1 }
                .forEachIndexed { y, line ->
                    line.forEachIndexed { x, c ->
                        val x2 = 2 * x
                        // @formatter:off
                        when (c) {
                            '.', '@' -> { put(x2 to y, c); put(x2+1 to y, '.') }
                            '#' -> { put(x2 to y, c); put(x2+1 to y, c) }
                            'O' -> { put(x2 to y, '['); put(x2+1 to y, ']') }
                            else -> error("won't happen")
                        }
                        // @formatter:on
                        Unit
                    }
                }
        }.toMutableMap()
        return MatrixDay15(maxX, maxY, MutableNotNullMap(points), moveSeq(input))
    }

    fun part1(input: List<String>) = toMatrix(input).startMove()

    fun part2(input: List<String>)= toMatrixPart2(input) //.alsoLog("init:")
        .part2StartMove()

    chkTestInput(Part1, smallTestInput, 2028) { part1(it) }
    chkTestInput(Part1, testInput, 10092) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 9021) { part2(it) }
    solve(Part2, input) { part2(it) }
}

class MatrixDay15(maxX: Int, maxY: Int, override val points: MutableNotNullMap<Pair<Int, Int>, Char>, val moveSeq: List<Direction>) : Matrix<Char>(maxX = maxX, maxY = maxY, points = points) {
    private var robot = findOneByValue('@')

    fun startMove(): Int {
        moveSeq.forEach { robotMoves(robot, it) }
        return findByValue('O').sumOf { (x, y) -> 100 * (y + 1) + x + 1 }
    }

    private fun robotMoves(cur: Pair<Int, Int>, direction: Direction) {
        val next = cur.move(direction)
        if (next.invalidPoint() || points[next] == '#') return
        if (points[next] == '.') {
            points[next] = '@'
            points[robot] = '.'
            robot = next
            return
        }
        if (points[next] == 'O') {
            val boxes = takeBoxesFrom(next, direction)
            if (boxes.isNotEmpty()) {
                points[robot] = '.'
                points[next] = '@'
                points[boxes.last().move(direction)] = 'O' //last +1
                robot = next
            }
        }
    }

    private fun takeBoxesFrom(from: Pair<Int, Int>, direction: Direction): List<Pair<Int, Int>> = when (direction) {
        //always from the "from" point: <-- [from] --> (ascending & descending)
        Right -> points.keys.filter { (x, y) -> x >= from.first && y == from.second }.sortedBy { it.first }
        Left -> points.keys.filter { (x, y) -> x <= from.first && y == from.second }.sortedByDescending { it.first }
        Down -> points.keys.filter { (x, y) -> x == from.first && y >= from.second }.sortedBy { it.second }
        Up -> points.keys.filter { (x, y) -> x == from.first && y <= from.second }.sortedByDescending { it.second }
    }.takeWhile { points[it] == 'O' }.takeIf { boxes -> boxes.last().move(direction).let { it.validPoint() && points[it] != '#' } }.orEmpty()

    // part2
    fun part2StartMove(): Int {
        moveSeq.forEachIndexed { i, dir -> part2RobotMoves(robot, dir)
//            alsoLog("after ${i + 1} : $dir")
        }
        return findByValue('[').sumOf { (x, y) -> 100 * (y + 1) + x + 2 }
    }


    private fun part2RobotMoves(cur: Pair<Int, Int>, direction: Direction) {
        val next = cur.move(direction)
        if (next.invalidPoint() || points[next] == '#') return
        if (points[next] == '.') {
            points[next] = '@'
            points[robot] = '.'
            robot = next
            return
        }
        if (points[next] in "[]") {
            if (direction.isHorizontal()) {
                val boxes = part2TakeHorizontalBoxes(next, direction)
                if (boxes.isNotEmpty()) {
                    points[robot] = '.'
                    points[next] = '@'
                    points[boxes.last().move(direction)] = if (direction == Right) ']' else '[' //last +1
                    boxes.drop(1).forEach { points[it] = if (points[it] == '[') ']' else '[' }
                    robot = next
                }
            } else { //vertical
                val boxesRows = part2TakeVerticalBoxes(next, direction)
                if (boxesRows.isNotEmpty()) {
                    boxesRows.reversed().forEach { row -> //reverse, since we start from the "furthest" row
                        row.forEach {
                            points[it.move(direction)] = points[it]
                            points[it] = '.'
                        }
                    }
                    points[robot] = '.'
                    points[next] = '@'
                    robot = next
                }
            }
        }
    }

    private fun part2TakeHorizontalBoxes(from: Pair<Int, Int>, direction: Direction): List<Pair<Int, Int>> {
        val boxes = when (direction) {//always from the "from" point: <-- [from] -->
            Right -> points.keys.filter { (x, y) -> x >= from.first && y == from.second }.sortedBy { it.first }
            Left -> points.keys.filter { (x, y) -> x <= from.first && y == from.second }.sortedByDescending { it.first }
            else -> error("horizontal directions only")
        }.takeWhile { points[it] in "[]" }
        if (boxes.last().move(direction).let { it.invalidPoint() || points[it] == '#' }) return emptyList()
        return boxes
    }

    private fun part2TakeVerticalBoxes(from: Pair<Int, Int>, direction: Direction): List<List<Pair<Int, Int>>> {
        val yRange = when (direction) {
            Up -> from.second - 1 downTo 0
            Down -> from.second + 1..maxY
            else -> error("vertical directions only")
        }

        val boxes = buildList {
            if (points[from] == '[') add(listOf(from, from.move(Right))) else add(listOf(from.move(Left), from))
            yRange.forEach { y ->
                val myRow = getBoxesOnMyRow(last(), y)
                if (myRow.isEmpty()) return@buildList
                add(myRow)
            }
        }
        if (boxes.last().first().move(direction).invalidPoint()) return emptyList()
        boxes.forEach { boxesInRow -> if (boxesInRow.any { b -> points[b.move(direction)] == '#' }) return emptyList() }
        return boxes
    }

    private fun getBoxesOnMyRow(prePos: List<Pair<Int, Int>>, myY: Int): List<Pair<Int, Int>> {
        if (myY < 0 || myY > maxY) return emptyList()
        return buildList {
            prePos.filter { points[it] == '[' }.forEach { (preX) ->
                listOf(preX - 1 to myY, preX to myY, preX + 1 to myY).filter { it.validPoint() && points[it] == '[' }
                    .forEach { add(it); add(it.first + 1 to it.second) }
            }
        }.distinct() // !!! I spent about 2 hours to detect this distinct() was missing!!!!!
    }
}
