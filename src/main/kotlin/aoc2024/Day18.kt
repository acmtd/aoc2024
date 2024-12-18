package aoc2024

import aoc2024.Day18.*
import kotlin.time.measureTime

typealias Routing<T, U> = List<Pair<T, Map<T, U>>>

class Day18 {
    data class Vec2(var x: Int, var y: Int) {
        companion object {
            fun fromString(s: String) = s.split(",").map { it.toInt() }.let { Vec2(it[0], it[1]) }
        }

        operator fun plus(delta: Vec2) = Vec2(x + delta.x, y + delta.y)

        fun next(): List<Vec2> {
            return listOf(
                this + Vec2(0, 1),
                this + Vec2(0, -1),
                this + Vec2(1, 0),
                this + Vec2(-1, 0)
            )
        }

        fun inBounds(gridSize: Int) = (x >= 0 && y >= 0 && x < gridSize && y < gridSize)
        override fun toString() = "$x,$y"
    }

    data class Graph<Vec2>(
        val vertices: Set<Vec2>,
        val edges: Map<Vec2, Set<Vec2>>,
        val weights: Map<Pair<Vec2, Vec2>, Int>
    )
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

fun <T> shortestPath(shortestPathTree: Map<T, T?>, start: T, end: T): List<T> {
    fun pathTo(start: T, end: T): List<T> {
        if (shortestPathTree[end] == null) return listOf(end)
        return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
    }

    return pathTo(start, end)
}

fun getRouting(nodes: List<Vec2>, bytes: List<Vec2>, gridSize: Int): Routing<Vec2, Vec2?> {
    val edges = buildMap {
        nodes.forEach { node ->
            put(node, node.next().filter { it.inBounds(gridSize) && it !in bytes }.map { it }.toSet())
        }
    }

    val weights = buildMap {
        edges.map { (nodeFrom, toSet) -> toSet.map { nodeTo -> put(Pair(nodeFrom, nodeTo), 1) } }
    }

    return listOf(Vec2(0, 0)).map { it to dijkstra(Graph(nodes.toSet(), edges, weights), it) }
}

private fun buildNodeList(gridSize: Int, bytes: List<Vec2>) =
    (0..<gridSize).flatMap { y -> (0..<gridSize).map { x -> Vec2(x, y) } }.filter { it !in bytes }

fun routeTo(origin: Vec2, destination: Vec2, routing: Routing<Vec2, Vec2?>) =
    shortestPath(routing.first { it.first == origin }.second, origin, destination)

fun shortestRoute(nodeMap: List<Vec2>, bytes: List<Vec2>, start: Vec2, finish: Vec2, gridSize: Int) =
    routeTo(start, finish, getRouting(nodeMap, bytes, gridSize))

fun minSteps(bytes: List<Vec2>, start: Vec2, finish: Vec2, gridSize: Int) =
    shortestRoute(buildNodeList(gridSize, bytes), bytes, start, finish, gridSize).size - 1

fun simulate(bytes: List<Vec2>, seconds: Int = bytes.size, gridSize: Int) =
    minSteps(bytes.take(seconds), Vec2(0, 0), Vec2(gridSize - 1, gridSize - 1), gridSize)

fun simulatePart2(bytes: List<Vec2>, gridSize: Int): String {
    val start = Vec2(0, 0)
    val finish = Vec2(gridSize - 1, gridSize - 1)

    // do binary search to find where the end stops being reachable
    var lastOk = 0
    var firstNotOk = bytes.size

    while ((firstNotOk - lastOk) > 1) {
        val nextToTry = lastOk + ((firstNotOk - lastOk) / 2)

        if (canReach(bytes, nextToTry, gridSize, start, finish)) {
            lastOk = nextToTry
        } else {
            firstNotOk = nextToTry
        }
    }

    return bytes[firstNotOk - 1].toString()
}

private fun canReach(bytes: List<Vec2>, sec: Int, gridSize: Int, start: Vec2, finish: Vec2): Boolean {
    val route = shortestRoute(buildNodeList(gridSize, bytes.take(sec)), bytes.take(sec), start, finish, gridSize)
    return (start in route && finish in route)
}

private fun drawGrid(bytes: List<Vec2>, size: Int) {
    (0..<size).joinToString("\n") { y ->
        (0..<size).joinToString("") { x -> if (bytes.any { it.x == x && it.y == y }) "#" else "." }
    }.println()
}

fun bytes(input: List<String>) = input.map { line -> Vec2.fromString(line) }

fun main() {
    val testInput = readAsLines("Day18_test")
    check(simulate(bytes(testInput), 12, 7) == 22)
    check(simulatePart2(bytes(testInput), 7) == "6,1")

    val input = readAsLines("Day18")
    measureTime { simulate(bytes(input), 1024, 71).println() }.also { it.println() }  // 400 ms
    measureTime { simulatePart2(bytes(input), 71).println() }.also { it.println() } // 1.5s
}