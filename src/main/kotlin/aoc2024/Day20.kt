package aoc2024

import aoc2024.Day20.*
import kotlin.math.abs
import kotlin.time.measureTime

class Day20 {
    data class Maze(val walls: List<Vec2>, val start: Vec2, val end: Vec2, val gridSize: Int) {
        private val racetrack =
            (0..<gridSize).flatMap { y -> (0..<gridSize).map { x -> Vec2(x, y) } }.filter { it !in walls }

        private val edges = buildMap {
            racetrack.forEach { node ->
                put(node, node.next().filter { it.inBounds(gridSize) && it !in walls }.map { it }.toSet())
            }
        }

        private val weights =
            buildMap { edges.map { (nodeFrom, toSet) -> toSet.map { nodeTo -> put(Pair(nodeFrom, nodeTo), 1) } } }

        val path = findPath(start, end, dijkstra(Graph(racetrack.toSet(), edges, weights), start))

        private fun <T> findPath(startNode: T, endNode: T, route: Map<T, T?>): List<T> {
            var node = endNode

            return buildList {
                while (route[node] != null && node != startNode) {
                    add(node)
                    node = route[node]!!
                }

                add(node)
            }
        }

        private fun dijkstra(graph: Graph<Vec2>, start: Vec2): Map<Vec2, Vec2?> {
            val set: MutableSet<Vec2> = mutableSetOf()

            val delta = graph.vertices.associateWith { Int.MAX_VALUE }.toMutableMap()
            delta[start] = 0

            val previous: MutableMap<Vec2, Vec2?> = graph.vertices.associateWith { null }.toMutableMap()

            while (set != graph.vertices) {
                val v: Vec2 = delta
                    .filter { !set.contains(it.key) }
                    .minBy { it.value }
                    .key

                graph.edges.getValue(v).minus(set).forEach { neighbor ->
                    val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

                    if (newPath < delta.getValue(neighbor)) {
                        delta[neighbor] = newPath
                        previous[neighbor] = v
                    }
                }

                set.add(v)
            }

            return previous.toMap()
        }

        fun cheatSavings(wall: Vec2) =
            wall.eitherSide(this).filter { it in racetrack }.map { path.indexOf(it) }.reduce { a, b -> abs(a - b) - 2 }

        fun viableCheats() =
            walls.filter { !it.isBorder(gridSize) }
                .filterNot { it.hasHorizontalNeighbours(walls) && it.hasVerticalNeighbours(walls) }

        companion object {
            fun fromInput(input: List<String>): Maze {
                var start: Vec2? = null
                var end: Vec2? = null
                val walls = mutableListOf<Vec2>()

                val gridSize = input.size

                input.indices.forEach { y ->
                    input.indices.forEach { x ->
                        when (input[y][x]) {
                            'S' -> start = Vec2(x, y)
                            'E' -> end = Vec2(x, y)
                            '#' -> walls.add(Vec2(x, y))
                        }
                    }
                }

                return Maze(walls, start!!, end!!, gridSize)
            }
        }
    }

    data class Vec2(var x: Int, var y: Int) {
        operator fun plus(delta: Vec2) = Vec2(x + delta.x, y + delta.y)

        fun next() = listOf(this + Vec2(0, 1), this + Vec2(0, -1), this + Vec2(1, 0), this + Vec2(-1, 0))
        private fun left() = Vec2(x - 1, y)
        private fun right() = Vec2(x + 1, y)
        private fun up() = Vec2(x, y - 1)
        private fun down() = Vec2(x, y + 1)

        fun inBounds(gridSize: Int) = (x >= 0 && y >= 0 && x < gridSize && y < gridSize)
        fun isBorder(gridSize: Int) = (x == 0 || y == 0 || x == gridSize - 1 || y == gridSize - 1)
        fun hasHorizontalNeighbours(others: List<Vec2>) = others.any { it == left() || it == right() }
        fun hasVerticalNeighbours(others: List<Vec2>) = others.any { it == up() || it == down() }

        fun distance(other: Vec2) = abs(other.x - x) + abs(other.y - y)
        fun eitherSide(maze: Maze) =
            if (hasHorizontalNeighbours(maze.walls)) listOf(up(), down()) else listOf(left(), right())
    }

    data class Graph<Vec2>(
        val vertices: Set<Vec2>,
        val edges: Map<Vec2, Set<Vec2>>,
        val weights: Map<Pair<Vec2, Vec2>, Int>
    )
}

fun main() {
    fun solve(input: List<String>, threshold: Int, maxCheat: Int): Int {
        val maze = Maze.fromInput(input)

        return maze.path.indices.sumOf { start ->
            (start..<maze.path.size).count { end ->
                val taxiCabDistance = maze.path[start].distance(maze.path[end])

                if (taxiCabDistance > maxCheat) {
                    false
                } else {
                    val trackDistance = (end - start)

                    val savings = (trackDistance - taxiCabDistance)
                    savings >= threshold
                }
            }
        }
    }

    fun part1(input: List<String>, threshold: Int): Int {
        val maze = Maze.fromInput(input)

        return maze.viableCheats()
            .map { maze.cheatSavings(it) }
            .count { it >= threshold }
    }

    fun part1UsingGeneralSolve(input: List<String>, threshold: Int): Int {
        return solve(input, threshold, 2)
    }

    fun part2(input: List<String>, threshold: Int): Int {
        return solve(input, threshold, 20)
    }

    val testInput = readAsLines("Day20_test")
    check(part1(testInput, 2) == 44)
    check(part1UsingGeneralSolve(testInput, 2) == 44)
    check(part2(testInput, 50) == 285)

    val input = readAsLines("Day20")
    measureTime { part1(input, 100).println() }.also { it.println() }
    measureTime { part1UsingGeneralSolve(input, 100).println() }.also { it.println() }
    measureTime { part2(input, 100).println() }.also { it.println() }
}
