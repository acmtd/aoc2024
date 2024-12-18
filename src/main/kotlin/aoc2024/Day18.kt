package aoc2024

import aoc2024.Day18.*

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

        fun inBounds(gridSize: Int): Boolean {
            return (x >= 0 && y >= 0 && x < gridSize && y < gridSize)
        }

        override fun toString(): String {
            return "$x,$y"
        }
    }

    data class State(val pos: Vec2, val visited: List<Vec2>) {}

    // bring in the heavy guns of dijkstra for this one
    data class Node(val pos: Vec2)

    data class Graph<Node>(
        val vertices: Set<Node>,
        val edges: Map<Node, Set<Node>>,
        val weights: Map<Pair<Node, Node>, Int>
    )
}

private fun dijkstra(graph: Graph<Node>, start: Node): Map<Node, Node?> {
    val S: MutableSet<Node> = mutableSetOf()

    val delta = graph.vertices.associateWith { Int.MAX_VALUE }.toMutableMap()
    delta[start] = 0

    val previous: MutableMap<Node, Node?> = graph.vertices.associateWith { null }.toMutableMap()

    while (S != graph.vertices) {
        val v: Node = delta
            .filter { !S.contains(it.key) }
            .minBy { it.value }
            .key

        graph.edges.getValue(v).minus(S).forEach { neighbor ->
            val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

            if (newPath < delta.getValue(neighbor)) {
                delta[neighbor] = newPath
                previous[neighbor] = v
            }
        }

        S.add(v)
    }

    return previous.toMap()
}

fun <Node> shortestPath(shortestPathTree: Map<Node, Node?>, start: Node, end: Node): List<Node> {
    fun pathTo(start: Node, end: Node): List<Node> {
        if (shortestPathTree[end] == null) return listOf(end)
        return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
    }

    return pathTo(start, end)
}

fun getRouting(nodeMap: Map<Vec2, Node>, bytes: List<Vec2>, gridSize: Int): Routing<Node, Node?> {
    val nodes = nodeMap.values

    // for each node, get the set of other nodes (also free spaces) it connects to
    val edges = buildMap {
        nodes.forEach { node ->
            put(
                node,
                node.pos.next().filter { it.inBounds(gridSize) && it !in bytes }.mapNotNull { nodeMap[it] }.toSet()
            )
        }
    }

    // need to map each Pair<from, to> to a weight
    val weights = buildMap {
        edges.map { (nodeFrom, toSet) ->
            toSet.map { nodeTo ->
                put(Pair(nodeFrom, nodeTo), 1)
            }
        }
    }

    return listOf(nodeMap[Vec2(0, 0)]!!).map { it to dijkstra(Graph(nodes.toSet(), edges, weights), it) }
}

private fun buildNodeMap(gridSize: Int, bytes: List<Vec2>): Map<Vec2, Node> {
    return buildMap {
        (0..<gridSize).forEach { y ->
            (0..<gridSize).forEach { x ->
                val pos = Vec2(x, y)

                if (pos !in bytes) {
                    put(pos, Node(pos))
                }
            }
        }
    }
}

fun routeTo(origin: Node, destination: Node, routing: Routing<Node, Node?>): List<Node> {
    return shortestPath(routing.first { it.first == origin }.second, origin, destination)
}

fun shortestRoute(nodeMap: Map<Vec2, Node>, bytes: List<Vec2>, start: Vec2, finish: Vec2, gridSize: Int): List<Node> {
    val routing = getRouting(nodeMap, bytes, gridSize)

    val startNode = nodeMap.getValue(start)
    val endNode = nodeMap.getValue(finish)

    return routeTo(startNode, endNode, routing)
}

fun minSteps(bytes: List<Vec2>, start: Vec2, finish: Vec2, gridSize: Int): Int {
    return shortestRoute(buildNodeMap(gridSize, bytes), bytes, start, finish, gridSize).size - 1
}

fun simulate(bytes: List<Vec2>, seconds: Int = bytes.size, gridSize: Int): Int {
    val fallenBytes = bytes.take(seconds)

//    drawGrid(gridSize, fallenBytes)

    // now it's a simple traversal puzzle like the other day
    return minSteps(fallenBytes, Vec2(0, 0), Vec2(gridSize - 1, gridSize - 1), gridSize)
}

fun simulatePart2(bytes: List<Vec2>, gridSize: Int): String {
    val start = Vec2(0, 0)
    val finish = Vec2(gridSize - 1, gridSize - 1)

    // possible value is between (minOk+1) and gridSize
    // need to do a binary search between them
    var lastOk = 0
    var firstNotOk = bytes.size

    while ((firstNotOk - lastOk) > 1) {
        val nextToTry = lastOk + ((firstNotOk - lastOk) / 2)

        val canReach = canReach(bytes, nextToTry, gridSize, start, finish)

        if (canReach) {
            lastOk = nextToTry
        } else {
            firstNotOk = nextToTry
        }
    }

    return bytes[firstNotOk - 1].toString()
}

private fun canReach(
    bytes: List<Vec2>,
    sec: Int,
    gridSize: Int,
    start: Vec2,
    finish: Vec2
): Boolean {
    val fallen = bytes.take(sec)
    val nodeMap = buildNodeMap(gridSize, fallen)

    val route = shortestRoute(nodeMap, bytes.take(sec), start, finish, gridSize).map { it.pos }
    val ok = (start in route && finish in route)

    return ok
}


private fun drawGrid(gridSize: Int, bytes: List<Vec2>) {
    (0..<gridSize).forEach { y ->
        (0..<gridSize).forEach { x ->
            if (bytes.any { it.x == x && it.y == y }) {
                print("#")
            } else {
                print(".")
            }
        }
        println("")
    }
}

fun bytes(input: List<String>): List<Vec2> {
    return input.map { line -> Vec2.fromString(line) }
}

fun main() {
    val testInput = readAsLines("Day18_test")
    check(simulate(bytes(testInput), 12, 7) == 22)
    check(simulatePart2(bytes(testInput), 7) == "6,1")

    val input = readAsLines("Day18")
    simulate(bytes(input), 1024, 71).println() // 234
    simulatePart2(bytes(input), 71).println() // 58, 19
}