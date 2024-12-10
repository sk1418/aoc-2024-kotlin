import kotlin.math.min

// https://adventofcode.com/2024/day/9
fun main() {
    val today = "Day09"

    val input = readInput(today)
    val testInput = readTestInput(today)

    fun part1(input: List<String>): Long = DiskDay09(input.first()).deFragPerBlock().mapIndexed { id, d -> id * d.toLong() }.sum()

    fun part2(input: List<String>): Long = DiskDay09(input.first()).deFragByFile().mapIndexed { id, d -> if (d > 0) id * d.toLong() else 0 }.sum()

    chkTestInput(Part1, testInput, 1928L) { part1(it) }
    solve(Part1, input) { part1(it) }

    chkTestInput(Part2, testInput, 2858L) { part2(it) }
    solve(Part2, input) { part2(it) }
}

data class DiskDay09(val line: String) {

    data class FileBlock(val id: Int, val blocks: Int) {
        val file = MutableList(blocks) { id }
        fun clear() = file.replaceAll { -1 }
    }

    data class FreeBlock(val blocks: Int) {
        val spaces = MutableList(blocks) { -1 }

        private fun capacity() = spaces.count { it < 0 }
        fun fillFile(fileBlock: FileBlock): Boolean = if (capacity() >= fileBlock.blocks) {
            var startIdx = blocks - capacity()
            fileBlock.file.forEach { spaces[startIdx++] = it }
            fileBlock.clear()
            true
        } else false
    }


    val fileList = line.filterIndexed { idx, _ -> idx % 2 == 0 }.mapIndexed { idx, c -> FileBlock(idx, c.digitToInt()) }
    val spaceList = line.filterIndexed { idx, _ -> idx % 2 == 1 }.map { FreeBlock(it.digitToInt()) }

    fun deFragPerBlock(): List<Int> {
        val fileBlockCnt = fileList.sumOf { it.blocks }
        val fillSeq = ArrayDeque(fileList.flatMap { it.file })
        var allocatedBlocks = 0

        return buildList {
            fileList.forEach { fileBlk ->
                if (allocatedBlocks == fileBlockCnt) return@buildList
                val requiredFileCnt = min(fileBlockCnt - allocatedBlocks, fileBlk.blocks)
                repeat(requiredFileCnt) { add(fileBlk.id) }
                allocatedBlocks += requiredFileCnt

                val requiredSpaceCnt = min(fileBlockCnt - allocatedBlocks, spaceList[fileBlk.id].blocks)
                if (requiredSpaceCnt > 0) {
                    repeat(requiredSpaceCnt) { add(fillSeq.removeLast()) }
                    allocatedBlocks += requiredSpaceCnt
                }
            }
        }
    }

    fun deFragByFile(): List<Int> = buildList {
        fileList.reversed().onEachIndexed { idx, fileBlock ->
            spaceList.slice(0..spaceList.lastIndex - idx).firstOrNull { it.fillFile(fileBlock) }
        }.reversed().forEachIndexed { idx, fileBlk ->
            addAll(fileBlk.file)
            if (idx in spaceList.indices) addAll(spaceList[idx].spaces)
        }
    }
}
