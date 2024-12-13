package aoc2024

import aoc2024.Day12.Direction.*
import aoc2024.Day12.Garden
import aoc2024.Day12.Position
import aoc2024.Day12.Region
import kotlin.math.absoluteValue

import kotlin.time.measureTime

class Day12 {
    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0),
        UP_LEFT(-1, -1), UP_RIGHT(1, -1), DOWN_LEFT(-1, 1), DOWN_RIGHT(1, 1)
    }

    data class Position(val x: Int, val y: Int) {
        fun neighbours() = listOf(move(UP), move(DOWN), move(LEFT), move(RIGHT))

        fun move(dir: Direction) = Position(x + dir.dx, y + dir.dy)

        fun isAdjacentTo(other: Position): Boolean {
            val dx = (x - other.x).absoluteValue
            val dy = (y - other.y).absoluteValue

            return (dx + dy) == 1
        }
    }

    data class Plot(val type: Char, val position: Position)
    data class Region(val type: Char, val positions: Set<Position>) {
        fun area() = positions.size
        fun perimeter() = positions.sumOf { 4 - adjacency(it) }

        private fun adjacency(p: Position) = positions.count { p2 -> p.isAdjacentTo(p2) }
        private fun hasAdjacent(p: Position, d: Direction) = p.move(d) in positions

        private fun corners(p: Position): List<Char> {
            return buildList {
                // these are the "easy" outer corners - top-left, top-right, bottom-left, bottom-right
                if (!hasAdjacent(p, UP) && !hasAdjacent(p, LEFT)) add('r')
                if (!hasAdjacent(p, UP) && !hasAdjacent(p, RIGHT)) add('7')
                if (!hasAdjacent(p, DOWN) && !hasAdjacent(p, LEFT)) add('L')
                if (!hasAdjacent(p, DOWN) && !hasAdjacent(p, RIGHT)) add(',')

                // these are the tricky inner corners that require looking at diagonal adjacency
                if (hasAdjacent(p, UP) && hasAdjacent(p, UP_RIGHT) && !hasAdjacent(p, RIGHT)) add('r')
                if (hasAdjacent(p, DOWN) && hasAdjacent(p, DOWN_RIGHT) && !hasAdjacent(p, RIGHT)) add('L')
                if (hasAdjacent(p, UP) && hasAdjacent(p, UP_LEFT) && !hasAdjacent(p, LEFT)) add('7')
                if (hasAdjacent(p, DOWN) && hasAdjacent(p, DOWN_LEFT) && !hasAdjacent(p, LEFT)) add(',')
            }
        }

        fun numberOfSides() = positions.map { it to corners(it) }.sumOf { it.second.size }
    }

    class Garden(val input: List<String>) {
        private val plots = input.indices.flatMap { row ->
            input[row].indices.map { col ->
                Plot(input[row][col], Position(col, row))
            }
        }

        fun types() = plots.map { it.type }.distinct()
        fun plots(type: Char) = plots.filter { it.type == type }
    }
}

fun main() {
    fun walkNeighbours(initial: Position, positions: List<Position>): MutableSet<Position> {
        val queue = ArrayDeque<Position>().apply { add(initial) }
        val visited = mutableSetOf<Position>()

        while (!queue.isEmpty()) {
            val current = queue.removeFirst()

            if (current !in visited) {
                visited.add(current)
                queue.addAll(current.neighbours().filter { it in positions })
            }
        }

        return visited
    }

    fun getRegions(input: List<String>): Set<Region> {
        val garden = Garden(input)
        val visited = mutableSetOf<Position>()

        return buildSet {
            garden.types().forEach { type ->
                val positions = garden.plots(type).map { it.position }

                garden.plots(type).forEach { plot ->
                    if (plot.position !in visited) {
                        walkNeighbours(plot.position, positions).apply {
                            visited.addAll(this)
                            add(Region(plot.type, this))
                        }
                    }
                }
            }
        }
    }

    fun part1(input: List<String>) = getRegions(input).sumOf { it.area() * it.perimeter() }
    fun part2(input: List<String>) = getRegions(input).sumOf { it.area() * it.numberOfSides() }

    val testInput = readAsLines("Day12_maintest")
    check(part1(testInput) == 1930)

    val input = readAsLines("Day12")
    measureTime { part1(input).println() }.also { it.println() }

    // run every possible test for part 2 to validate corner identification logic
    check(part2(readAsLines("Day12_part2_test1")) == 80)
    check(part2(readAsLines("Day12_part2_test2")) == 236)
    check(part2(readAsLines("Day12_part2_test3")) == 368)
    check(part2(readAsLines("Day12_part2_test4")) == 436)

    check(part2(testInput) == 1206)

    measureTime { part2(input).println() }.also { it.println() }
}