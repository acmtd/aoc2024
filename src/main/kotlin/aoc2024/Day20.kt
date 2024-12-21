package aoc2024

import aoc2024.Day20.*

class Day20 {
    data class Maze(val walls: List<Vec2>, val start: Vec2, val end: Vec2, val gridSize: Int) {
        val racetrack = (0..<gridSize).flatMap { y -> (0..<gridSize).map { x -> Vec2(x, y) } }.filter { it !in walls }

        private val edges = buildMap {
            racetrack.forEach { node ->
                put(node, node.next().filter { it.inBounds(gridSize) && it !in walls }.map { it }.toSet())
            }
        }

        private val weights = buildMap {
            edges.map { (nodeFrom, toSet) -> toSet.map { nodeTo -> put(Pair(nodeFrom, nodeTo), 1) } }
        }

        private val graph = Graph(racetrack.toSet(), edges, weights)
        private val route = dijkstra(graph, start)

        fun shortestRoute(p1: Vec2 = start, p2: Vec2 = end) = shortestPath(p1, p2, route)

        private fun <T> shortestPath(startNode: T, endNode: T, route: Map<T, T?>): List<T> {
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
        fun left() = Vec2(x - 1, y)
        fun right() = Vec2(x + 1, y)
        fun up() = Vec2(x, y - 1)
        fun down() = Vec2(x, y + 1)

        fun inBounds(gridSize: Int) = (x >= 0 && y >= 0 && x < gridSize && y < gridSize)
        fun isBorder(gridSize: Int) = (x == 0 || y == 0 || x == gridSize - 1 || y == gridSize - 1)
        fun hasHorizontalNeighbours(others: List<Vec2>) = others.any { it == left() || it == right() }
        fun hasVerticalNeighbours(others: List<Vec2>) = others.any { it == up() || it == down() }
    }

    data class Graph<Vec2>(
        val vertices: Set<Vec2>,
        val edges: Map<Vec2, Set<Vec2>>,
        val weights: Map<Pair<Vec2, Vec2>, Int>
    )
}

fun main() {
    fun part1(input: List<String>, threshold: Int): Int {
        val maze = Maze.fromInput(input)

        val originalPath = maze.shortestRoute()

        val possibleCheats = maze.walls
            .filter { wall -> !wall.isBorder(maze.gridSize) }
            .filterNot { wall -> wall.hasHorizontalNeighbours(maze.walls) && wall.hasVerticalNeighbours(maze.walls) }

        val savingsMap = mutableMapOf<Int, Int>()

        possibleCheats.forEach { wallToRemove ->
            val beforeAndAfter = buildList {
                if (wallToRemove.hasHorizontalNeighbours(maze.walls)) {
                    add(wallToRemove.up())
                    add(wallToRemove.down())
                }
                if (wallToRemove.hasVerticalNeighbours(maze.walls)) {
                    add(wallToRemove.left())
                    add(wallToRemove.right())
                }
            }.filter { it in maze.racetrack }

            val routeToWall = beforeAndAfter.map { it to maze.shortestRoute(maze.start, it) }.minBy { it.second.size }

            val otherSide = beforeAndAfter.first { it != routeToWall.first }
            val routeFromWall = maze.shortestRoute(otherSide, maze.end)
            val newPathLength = routeToWall.second.size + routeFromWall.size + 1

            val savings = originalPath.size - newPathLength

            if (savings >= threshold) {
                savingsMap[savings] = savingsMap.getOrDefault(savings, 0) + 1
            }
        }

        return savingsMap.filter { it.key >= threshold }.map { it.value }.sum()
    }

    val testInput = readAsLines("Day20_test")
    check(part1(testInput, 2) == 44)

    val input = readAsLines("Day20")
    part1(input, 100).println() // 1463
}

