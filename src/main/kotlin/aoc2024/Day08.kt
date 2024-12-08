package aoc2024

fun main() {
    fun <T> combinations(arr: List<T>): Sequence<Pair<T, T>> = sequence {
        for (i in 0 until arr.size - 1)
            for (j in i + 1 until arr.size)
                yield(arr[i] to arr[j])
    }

    data class Position(val row: Int, val col: Int)

    fun findAntinodes(input: List<String>, part1: Boolean): Int {
        val gridSize = input.size

        val antennaPositions = hashMapOf<Char, MutableList<Position>>()
        input.indices.forEach { rowNum ->
            input[rowNum].indices.forEach { colNum ->
                val ch = input[rowNum][colNum]
                if (ch != '.') {
                    antennaPositions.getOrPut(ch) { mutableListOf() }.add(Position(rowNum, colNum))
                }
            }
        }

        val antinodePositions = mutableSetOf<Position>()

        antennaPositions.forEach { (_, positions) ->
            combinations(positions).forEach { p ->
                // find out the separation between the two elements
                val deltaRow = p.second.row - p.first.row
                val deltaCol = p.second.col - p.first.col

                if (part1) {
                    antinodePositions.addAll(buildList {
                        add(Position(p.second.row + deltaRow, p.second.col + deltaCol))
                        add(Position(p.first.row - deltaRow, p.first.col - deltaCol))
                    }.filter { it.row >= 0 && it.col >= 0 && it.row < gridSize && it.col < gridSize })
                }
                else {
                    val multipliers = listOf(1, -1)
                    multipliers.forEach { m ->
                        var pos = p.first
                        do {
                            antinodePositions.add(pos)
                            pos = Position(pos.row + (m * deltaRow), pos.col + (m * deltaCol))
                        } while (pos.row >= 0 && pos.col >= 0 && pos.row < gridSize && pos.col < gridSize)
                    }
                }
            }
        }

        return antinodePositions.size
    }

    fun part1(input: List<String>): Int {
        return findAntinodes(input, true)
    }

    fun part2(input: List<String>): Int {
        return findAntinodes(input, false)
    }

    val testInput = readAsLines("Day08_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    val input = readAsLines("Day08")
    part1(input).println() // 244
    part2(input).println() // 912
}

