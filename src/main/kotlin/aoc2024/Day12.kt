package aoc2024

import aoc2024.Day12.Plant
import aoc2024.Day12.Position
import aoc2024.Day12.Plot
import kotlin.math.absoluteValue

import kotlin.time.measureTime

class Day12 {
    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0),
        UP_LEFT(-1, -1),
        UP_RIGHT(1, -1),
        DOWN_LEFT(-1, 1),
        DOWN_RIGHT(1, 1)
    }

    data class Position(val x: Int, val y: Int) {
        fun neighbours(): List<Position> {
            return listOf(
                Position(x + 1, y),
                Position(x - 1, y),
                Position(x, y - 1),
                Position(x, y + 1)
            )
        }

        fun move(dir: Direction) = Position(x + dir.dx, y + dir.dy)

        fun isAdjacentTo(other: Position): Boolean {
            val dx = (x - other.x).absoluteValue
            val dy = (y - other.y).absoluteValue

            return (dx + dy) == 1
        }
    }

    data class Plant(val type: Char, val position: Position) {}
    data class Plot(val type: Char, val positions: Set<Position>) {
        fun area() = positions.size
        fun perimeter() = positions.sumOf { 4 - adjacency(it) }

        private fun adjacency(p: Position) = positions.count { p2 -> p.isAdjacentTo(p2) }
        private fun adjacent(p: Position, d: Direction) = p.move(d) in positions

        private fun corners(p: Position): List<Char> {
            return buildList {
                if (!adjacent(p, Direction.UP) && !adjacent(p, Direction.LEFT)) add('r')
                if (!adjacent(p, Direction.UP) && !adjacent(p, Direction.RIGHT)) add('7')
                if (!adjacent(p, Direction.DOWN) && !adjacent(p, Direction.LEFT)) add('L')
                if (!adjacent(p, Direction.DOWN) && !adjacent(p, Direction.RIGHT)) add(',')

                if (adjacent(p, Direction.UP) && adjacent(p, Direction.UP_RIGHT) && !adjacent(p, Direction.RIGHT)) add('r')
                if (adjacent(p, Direction.DOWN) && adjacent(p, Direction.DOWN_RIGHT) && !adjacent(p, Direction.RIGHT)) add('L')
                if (adjacent(p, Direction.UP) && adjacent(p, Direction.UP_LEFT) && !adjacent(p, Direction.LEFT)) add('7')
                if (adjacent(p, Direction.DOWN) && adjacent(p, Direction.DOWN_LEFT) && !adjacent(p, Direction.LEFT)) add(',')
            }
        }

        fun numberOfSides(): Int {
            return positions.map { it to corners(it) }.sumOf { it.second.size }
        }
    }
}

fun main() {
    fun parse(input: List<String>): List<Plant> {
        return input.indices.flatMap { row ->
            input[row].indices.map { col ->
                Plant(input[row][col], Position(col, row))
            }
        }
    }

    fun walkNeighbours(initial: Position, map: Map<Position, Plant>): MutableSet<Position> {
        val queue = ArrayDeque<Position>()
        queue.add(initial)

        val visited = mutableSetOf<Position>()

        while (!queue.isEmpty()) {
            val current = queue.removeFirst()

            if (current !in visited) {
                visited.add(current)

                // need to get the neighboring plants and see if they should be assigned to the same plot
                val next = current.neighbours()
                    .filter { it in map.keys }
                    .filter { it !in visited }

                queue.addAll(next)
            }
        }

        return visited
    }

    fun getPlots(input: List<String>): MutableSet<Plot> {
        val plants = parse(input)

        val plots = mutableSetOf<Plot>()

        val types = plants.map { it.type }.distinct()

        types.forEach { type ->
            val plantsOfType = plants.filter { it.type == type }
            val plantMap = plantsOfType.associateBy { it.position }

            val visited = mutableSetOf<Position>()

            plantsOfType.forEach { plant ->
                if (plant.position !in visited) {
                    val region = walkNeighbours(plant.position, plantMap)
                    visited.addAll(region)

                    val newPlot = Plot(plant.type, region)
                    plots.add(newPlot)
                }
            }
        }

        return plots
    }

    fun part1(input: List<String>): Int {
        return getPlots(input).sumOf { it.area() * it.perimeter() }
    }

    fun part2(input: List<String>): Int {
        return getPlots(input).sumOf { it.area() * it.numberOfSides() }
    }

    check(part2(readAsLines("Day12_part2_test1")) == 80)
    check(part2(readAsLines("Day12_part2_test2")) == 236)
    check(part2(readAsLines("Day12_part2_test3")) == 368)
    check(part2(readAsLines("Day12_part2_test4")) == 436)

    val testInput = readAsLines("Day12_maintest")
    check(part1(testInput) == 1930)
    check(part2(testInput) == 1206)

    val input = readAsLines("Day12")
    measureTime { part1(input).println() }.also { it.println()} // 75ms
    measureTime { part2(input).println() }.also { it.println()} // 24ms
}