package aoc2024

fun main() {
    fun parse(blocks: List<String>): Pair<List<List<Int>>, List<List<Int>>> {
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()

        blocks.forEach { block ->
            val lines = block.lines()
            val cols = 0..<lines.first().length

            if (lines.first().all { it == '#' }) {
                cols.map { col -> lines.drop(1).count { l -> l[col] == '#' } }.apply { locks.add(this) }
            } else {
                cols.map { col -> lines.dropLast(1).count { l -> l[col] == '#' } }.apply { keys.add(this) }
            }
        }

        return Pair(locks, keys)
    }

    fun overlaps(lock: List<Int>, key: List<Int>) = lock.indices.any { lock[it] + key[it] > 5 }

    fun part1(input: List<String>): Int {
        val (locks, keys) = parse(input)
        return locks.flatMap { l -> keys.filterNot { k -> overlaps(l, k) }.map { k -> Pair(l, k) } }.count()
    }

    val testInput = readAsBlocks("Day25_test")
    check(part1(testInput) == 3)

    val input = readAsBlocks("Day25")
    part1(input).println()
}