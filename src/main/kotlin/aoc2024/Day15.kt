package aoc2024

import aoc2024.Day15.*

class Day15 {
    class Warehouse(val input: String, val part1: Boolean) {
        private val items = input.lines().mapIndexed { y, line ->
            line.flatMapIndexed { x, char ->
                when (char) {
                    'O' -> {
                        if (part1) {
                            listOf(Box(Vec2(x, y)))
                        } else {
                            listOf(BigBox(Vec2(x * 2, y)))
                        }
                    }

                    '#' -> {
                        if (part1) {
                            listOf(Wall(Vec2(x, y)))
                        } else {
                            listOf(Wall(Vec2(x * 2, y)), Wall(Vec2(x * 2 + 1, y)))
                        }
                    }

                    '@' -> {
                        if (part1) {
                            listOf(Robot(Vec2(x, y)))
                        } else {
                            listOf(Robot(Vec2(x * 2, y)))
                        }
                    }

                    else -> emptyList()
                }
            }
        }.flatten()

        private val maxX = items.map { it.pos }.maxOf { it.x }
        private val maxY = items.map { it.pos }.maxOf { it.y }
        private val wallPositions = items.filter { !it.canMove() }.map { it.pos }

        private val robot = items.first { it is Robot }

        private fun moveableItems(dir: Vec2, item: Item, maybeMovable: Set<Item>): Set<Item> {
            val newPositions = item.positions().map { it + dir }
            if (newPositions.any { it in wallPositions }) return emptySet()

            val nextItems =
                items.filter { it.canMove() && it != item && it.positions().any { p -> p in newPositions } }

            if (nextItems.isEmpty()) return maybeMovable

            return nextItems.flatMap { next ->
                val items = moveableItems(dir, next, maybeMovable + next)
                items.ifEmpty { return emptySet() }
            }.toSet()
        }

        fun makeMove(dir: Vec2) {
            moveableItems(dir, robot, setOf(robot)).forEach { item -> item.pos += dir }
        }

        fun gps() = items.filter { it is Box || it is BigBox }.sumOf { it.pos.y * 100 + it.pos.x }

        fun draw() {
            var lastWasBigBox = false

            val map = items.associateBy { it.pos }

            for (y in 0..maxY) {
                for (x in 0..maxX) {
                    when (map[Vec2(x, y)]) {
                        null -> {
                            if (!lastWasBigBox) print(".")
                            lastWasBigBox = false
                        }

                        is Wall -> print("#")
                        is Box -> print("O")
                        is Robot -> print("@")

                        is BigBox -> {
                            print("[]")
                            lastWasBigBox = true
                        }
                    }
                }
                println("")
            }
        }
    }

    sealed class Item(var pos: Vec2) {
        abstract fun canMove(): Boolean
        open fun positions() = listOf(pos)
    }

    class Box(pos: Vec2) : Item(pos) {
        override fun canMove() = true
    }

    class BigBox(pos: Vec2) : Item(pos) {
        override fun canMove() = true
        override fun positions() = listOf(pos, Vec2(pos.x + 1, pos.y))
    }

    class Wall(pos: Vec2) : Item(pos) {
        override fun canMove() = false
    }

    class Robot(pos: Vec2) : Item(pos) {
        override fun canMove() = true
    }

    data class Vec2(val x: Int, val y: Int) {
        companion object {
            fun move(char: Char) = when (char) {
                '^' -> Vec2(0, -1)
                'v' -> Vec2(0, 1)
                '<' -> Vec2(-1, 0)
                '>' -> Vec2(1, 0)
                else -> null
            }
        }

        operator fun plus(offset: Vec2) = Vec2(x + offset.x, y + offset.y)
    }
}

fun run(input: List<String>, part1: Boolean): Int {
    val warehouse = Warehouse(input.first(), part1)
    val moveList = input.last().mapNotNull { Vec2.move(it) }

    moveList.forEach { warehouse.makeMove(it) }

    println("END POSITION")
    warehouse.draw()
    return warehouse.gps()
}

fun main() {
    val testInput = readAsBlocks("Day15_test")
    check(run(testInput, true) == 10092)
    check(run(testInput, false) == 9021)

    val input = readAsBlocks("Day15")
    run(input, true).println()
    run(input, false).println()
}
