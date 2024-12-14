package aoc2024

import aoc2024.Day14.*

class Day14 {
    data class Result(val seconds: Int, val map: String, val score: Int)

    data class Vec2(var x: Int, var y: Int) {
        companion object {
            fun fromString(s: String) = s.split(",").map { it.toInt() }.let { Vec2(it[0], it[1]) }
        }

        operator fun plus(delta: Vec2) = Vec2(x + delta.x, y + delta.y)
    }

    data class Robot(var pos: Vec2, val vel: Vec2) {
        private fun move() {
            pos += vel
        }

        private fun teleport(space: Space) {
            pos = Vec2(pos.x.mod(space.dimensions.x), pos.y.mod(space.dimensions.y))
        }

        fun moveAndTeleport(space: Space) {
            move()
            teleport(space)
        }
    }

    data class Space(val dimensions: Vec2) {
        private fun lines() = 0..<dimensions.y
        private fun cols() = 0..<dimensions.x

        fun map(robots: List<Robot>): String {
            return lines().joinToString("\n") { mapLine(robots, it) }
        }

        private fun mapLine(robots: List<Robot>, y: Int): String {
            return buildString {
                cols().forEach { x ->
                    val count = robots.filter { it.pos == Vec2(x, y) }.size
                    if (count == 0) {
                        append(".")
                    } else append(count)
                }
            }
        }

        private fun quadrants(): List<Pair<IntRange, IntRange>> {
            val left = 0..<(dimensions.x - 1) / 2
            val right = dimensions.x / 2 + 1..<dimensions.x
            val top = 0..<(dimensions.y - 1) / 2
            val bottom = dimensions.y / 2 + 1..<dimensions.y

            return listOf(left to top, left to bottom, right to top, right to bottom)
        }

        fun quadrantCounts(robots: List<Robot>): List<Int> {
            return quadrants().map { (xRange, yRange) -> robots.count { it.pos.x in xRange && it.pos.y in yRange } }
        }

        fun clusteringScore(robots: List<Robot>) = lines().sumOf { y ->
            robots.asSequence().filter { it.pos.y == y }.map { it.pos.x }.sorted().zipWithNext()
                .sumOf { it.second - it.first }
        }
    }
}

fun main() {
    fun parse(input: List<String>): List<Robot> {
        val positions = input.map { it.substringBefore(" ").substringAfter("=").let { s -> Vec2.fromString(s) } }
        val velocities = input.map { it.substringAfter(" ").substringAfter("=").let { s -> Vec2.fromString(s) } }

        return positions.zip(velocities).map { (p, v) -> Robot(p, v) }
    }

    fun part1(input: List<String>, space: Space): Int {
        val robots = parse(input)

        repeat(100) { robots.forEach { it.moveAndTeleport(space) } }

        return space.quadrantCounts(robots).reduce(Int::times)
    }

    fun part2(input: List<String>, space: Space): Result {
        val robots = parse(input)

        var best = Result(0, "", Int.MAX_VALUE)

        (1..space.dimensions.x * space.dimensions.y).forEach { iteration ->
            robots.forEach { it.moveAndTeleport(space) }

            space.clusteringScore(robots).let { score ->
                if (score < best.score) best = Result(iteration, space.map(robots), score)
            }
        }

        return best
    }

    val testInput = readAsLines("Day14_test")
    check(part1(testInput, Space(Vec2(11, 7))) == 12)

    val input = readAsLines("Day14")
    part1(input, Space(Vec2(101, 103))).println()
    part2(input, Space(Vec2(101, 103))).apply {
        println(this.map)
        println(this.seconds)
    }
}

