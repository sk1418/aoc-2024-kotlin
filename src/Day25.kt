// https://adventofcode.com/2024/day/25
fun main() {
    val today = "Day25"

    val input = readInput(today)
    val testInput = readTestInput(today)
    
    fun parseLocksAndKeys(input: List<String>): Pair<List<Set<Pair<Int, Int>>>, List<Set<Pair<Int, Int>>>> {
        val (locks, keys) = mutableListOf<Set<Pair<Int, Int>>>() to mutableListOf<Set<Pair<Int, Int>>>()
        var theInput = input
        while (theInput.isNotEmpty()) {
            theInput.takeWhile { it.isNotBlank() }.let { chunk ->
                buildSet {
                    chunk.forEachIndexed { y, line -> line.forEachIndexed { x, c -> if (c == '#') add(x to y) } }
                }.let { if ('#' in chunk.first()) locks.add(it) else keys.add(it) }
                theInput = theInput.drop(chunk.size + 1)
            }
        }
        return locks to keys
    }

    fun part1(input: List<String>): Int {
        val (theLocks, theKeys) = parseLocksAndKeys(input)
        return theKeys.sumOf { keyPairs ->
            theLocks.count { lockPairs -> lockPairs.intersect(keyPairs).isEmpty() }
        }
    }

    chkTestInput(Part1, testInput, 3) { part1(it) }
    solve(Part1, input) { part1(it) }

    //Day 25 doesn't have Part2, YEAH! Merry Christmas!
}