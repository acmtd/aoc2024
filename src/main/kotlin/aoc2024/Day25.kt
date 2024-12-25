package aoc2024

fun main() {
    fun parse(blocks: List<String>): Pair<List<List<Int>>, List<List<Int>>> {
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()

        blocks.forEach { block ->
            val lines = block.lines()

            val maxCol = lines.first().length - 1

            if (lines.first().all { it == '#' }) {
                val heights = (0..maxCol).map { col ->
                     lines.drop(1).count { l -> l[col] == '#' }
                }

                locks.add(heights)
            } else {
                val heights = (0..maxCol).map { col ->
                    lines.dropLast(1).count { l -> l[col] == '#' }
                }

                keys.add(heights)
            }
        }

        return Pair(locks, keys)
    }

    fun overlaps(lock: List<Int>, key: List<Int>): Boolean {
        return lock.indices.any { lock[it] + key[it] > 5 }
    }

    fun part1(input: List<String>): Int {
        val (locks, keys) = parse(input)

        val pairs = locks.flatMap { l ->
                keys.filterNot { k -> overlaps(l, k) }
                    .map { k -> Pair(l, k) }
        }

        return pairs.count()
    }

    val testInput = readAsBlocks("Day25_test")
    check(part1(testInput) == 3)

    val input = readAsBlocks("Day25")
    part1(input).println()
}

