import GateOp.*

// https://adventofcode.com/2024/day/24

fun main() {
    val today = "Day24"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun part1(input: List<String>) = Day24Solution(input).zNumber()

    fun part2(input: List<String>): String {
        // manually check the exception message and fix(swap) in the input file
        // try 4 times and found the solution.
        Day24Solution(input).expectedResult()
        return listOf("nwq", "z36", "mdb", "z22", "fvw", "z18", "wpq", "grf").sorted().joinToString(",")
// solution:   fvw,grf,mdb,nwq,wpq,z18,z22,z36
    }

    chkTestInput(Part1, testInput, 2024L) { part1(it) }
    solve(Part1, input) { part1(it) }

//    chkTestInput(Part2, testInput, 0L) { part2(it) }
    solve(Part2, input) { part2(it) }
}


private enum class GateOp { XOR, AND, OR }
private class Day24Solution(val input: List<String>) {
    private val varMap = buildMap {
        input.takeWhile { it.isNotBlank() }.forEach { it.split(": ").also { (v, i) -> put(v, i.toInt()) } }
    }.toMutableMap()


    private val operationMap = buildMap {
        input.takeLastWhile { it.isNotBlank() }.forEach {
            it.split(" -> ").also { (oStr, vName) ->
                oStr.split(" ").also { (a, op, b) ->
                    put(vName) { calc(a, b, GateOp.valueOf(op)) }
                }
            }
        }
    }

    private fun getOrCalc(name: String): Int = varMap.getOrPut(name) { operationMap.getValue(name).invoke() }

    private fun calc(a: String, b: String, op: GateOp): Int = (getOrCalc(a) to getOrCalc(b)).let { (v1, v2) ->
        when (op) {
            AND -> v1 and v2
            OR -> v1 or v2
            XOR -> v1 xor v2
        }
    }

    fun zNumber() = operationMap.keys.filter { it.startsWith("z") }
        .map { it to getOrCalc(it) }.sortedByDescending { it.first }
        .fold(initial = 0L) { acc, (_, bit) -> acc shl 1 or bit.toLong() }

    //part2
    private val gateMap = buildMap {
        input.takeLastWhile { it.isNotBlank() }.forEach {
            it.split(" -> ").also { (oStr, vName) ->
                oStr.split(" ").also { (a, op, b) ->
                    put(setOf(a, b) to GateOp.valueOf(op), vName)
                }
            }
        }
    }.toNotNullMap()


    /**
     * Bit 00:
     *
     *     x00 XOR y00 -> z00
     *     x00 AND y00 -> finalCarry
     *
     * Every bit after 00:
     *
     *     x01 XOR y01 -> firstResult
     *     x01 AND y01 -> firstCarry
     *     firstResult XOR previousFinalCarry -> z01
     *     firstResult AND previousFinalCarry -> secondCarry
     *     firstCarry OR secondCarry -> finalCarry
     *
     */
    val xBits = varMap.keys.filter { it.startsWith("x") }.map { it to getOrCalc(it) }.sortedBy { it.first }.map { it.second }
    val finalCarry = MutableList(xBits.size) { "" }

    class Day24Ex(strPair: Pair<String, String>) : Exception("Swap between: $strPair")

    private fun check(bitIdx: Int): Result<Unit> {
        return kotlin.runCatching {
            if (bitIdx == 0) {
                val myInput = setOf("x00", "y00")
                if (gateMap[myInput to XOR] != "z00") {
                    throw Day24Ex("x00 XOR y00 -> [Result]" to "z00")
                }
                finalCarry[bitIdx] = gateMap.getValue(myInput to AND)
            } else {
                val padIdx = "$bitIdx".padStart(2, '0')
                val myInput = setOf("x$padIdx", "y$padIdx")
                val preCarry = finalCarry[bitIdx - 1]
                val firstResult = gateMap[myInput to XOR]
                "x$padIdx XOR y$padIdx -> $firstResult".alsoLog("firstResult")

                val firstCarry = gateMap[myInput to AND]
                "x$padIdx AND y$padIdx -> $firstCarry".alsoLog("firstCarry")

                if (gateMap[setOf(firstResult, preCarry) to XOR] != "z$padIdx") {
                    throw Day24Ex("$firstResult XOR $firstCarry -> [Result]" to "z$padIdx")
                }
                "$firstResult XOR $preCarry -> z$padIdx".alsoLog("z$padIdx")
                val secondCarry = gateMap[setOf(firstResult, preCarry) to AND]
                "$firstResult AND $preCarry -> $secondCarry".alsoLog("secondCarry")

                finalCarry[bitIdx] = gateMap[setOf(firstCarry, secondCarry) to OR]
                "$firstCarry AND $secondCarry -> ${finalCarry[bitIdx]}".alsoLog("finalCarry")
            }
        }
    }

    fun expectedResult() {
        xBits.indices.asSequence().map { check(it) }.firstOrNull { it.isFailure }?.exceptionOrNull()?.printStackTrace()
    }
}