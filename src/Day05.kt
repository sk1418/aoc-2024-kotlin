// https://adventofcode.com/2024/day/5
fun main() {
    val today = "Day05"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun parseInput(input: List<String>): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        val (ruleLines, updateLines) = input.partition { '|' in it }
        val rules = ruleLines.map { it.split("|").let { it[0].toInt() to it[1].toInt() } }
        val updates = updateLines.drop(1).map { it.toInts(sep = ",") }
        return rules to updates
    }

    fun correctUpdates(updates: List<List<Int>>, rules: List<Pair<Int, Int>>) = updates.filter { update ->
        var myRules = rules.filter { rule -> rule.first in update && rule.second in update }
        update.asSequence().map { n ->
            myRules = myRules.filter { it.first != n }
            myRules.none { it.second == n }
        }.all { it }
    }

    fun part1(input: List<String>): Int {
        val (rules, updates) = parseInput(input)
        return correctUpdates(updates, rules).sumOf { it[it.size / 2] }
    }

    fun part2(input: List<String>): Int {
        val (rules, updates) = parseInput(input)
        val tobeFixed = (updates - correctUpdates(updates, rules).toSet())
        return tobeFixed.map { it.toMutableList() }.sumOf { update ->
            for (i in update.indices) {
                for (j in i + 1..update.lastIndex) {
                    val (current, later) = update[i] to update[j]
                    if (rules.any { rule -> rule.first == later && rule.second == current }) {//swap
                        update[i] = later
                        update[j] = current
                    }
                }
            }
            update[update.size / 2]
        }
    }

    chkTestInput(part1(testInput), 143, Part1)
    println("[Part1]: ${part1(input)}")

    chkTestInput(part2(testInput), 123, Part2)
    println("[Part2]: ${part2(input)}")
}
