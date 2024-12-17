// https://adventofcode.com/2024/day/17
fun main() {
    val today = "Day17"

    val input = readInput(today)
    val testInput = readTestInput(today)
    val testInputPart2 = readTestInput("$today-part2")

    fun parseInput(input: List<String>) = Program(
        input[0].substringAfterLast(':').trim().toLong(),
        input[1].substringAfterLast(':').trim().toLong(),
        input[2].substringAfterLast(':').trim().toLong(),
        input.last().substringAfterLast(':').trim().toInts(",")
    )

    fun part1(input: List<String>) = parseInput(input).run().joinToString(",")

    fun part2(input: List<String>): Long = parseInput(input).let { program -> program findTheAForOutput program.theSequence }

    chkTestInput(Part1, testInput, "4,6,3,5,6,3,5,2,1,0") { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInputPart2, 117440L) { part2(it) }
    solve(Part2, input) { part2(it) }
}

data class Program(var a: Long, var b: Long = 0, var c: Long = 0, val theSequence: List<Int>) {
    private val output: MutableList<Int> = mutableListOf()
    private var loopRequired = true
    private val instructions = listOf(
        { o: Int -> a = a shr comboOperand(o).toInt() }, // a /= 2 pow comboOper(o)
        { o: Int -> b = b xor o.toLong() },
        { o: Int -> b = comboOperand(o) % 8 },
        { o: Int -> loopRequired = (a != 0L) }, // input has always 3,0 at the end. so do the whole operations again
        { o: Int -> b = b xor c },
        { o: Int -> output.add((comboOperand(o) % 8).toInt()) }, //outputs
        { o: Int -> b = a shr comboOperand(o).toInt() },
        { o: Int -> c = a shr comboOperand(o).toInt() }
    )

    private fun comboOperand(o: Int) = when (o) {
        in 0..3 -> o.toLong()
        4 -> a
        5 -> b
        6 -> c
        else -> error("Invalid combo-operand $o")
    }

    fun run(): List<Int> {
        while (loopRequired) {
             theSequence.chunked(2).forEach { op ->
                instructions[op.first()].invoke(op.last())
            }
        }
        return output
    }

    private fun runWithGivenA(givenA: Long): List<Int> = Program(givenA, theSequence = theSequence).run()

    /**
     * My Input:
     *
     *    2,4 b = a mod 8
     *    1,3 b = b xor 3
     *    7,5 c = a shr b
     *    4,7 b= b xor c
     *    0,3 a = a shr 3 <---!!
     *    1,5 b = b xor 5
     *    5,5 output b
     *    3,0 loop
     *
     *  (!!) only this operation writes to `a` in each loop, so in every loop we take (a shl 3) as the base
     */
    infix fun findTheAForOutput(target: List<Int>): Long {
        var myA = when (target.size) {
            1 -> 0
            else -> findTheAForOutput(target.drop(1)) shl 3
        }//.alsoLog("Output and A"){"$target and a: $it"}
        while (runWithGivenA(myA) != target) {
            myA++
        }
        return myA
    }
}
