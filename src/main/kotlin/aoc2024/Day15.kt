package aoc2024

import aoc2024.Day15.*

class Day15 {
    class Warehouse(val input: String) {
        private val items = input.lines().mapIndexed { y, line ->
            line.mapIndexedNotNull { x, char ->
                when (char) {
                    'O' -> Box(Vec2(x, y))
                    '#' -> Wall(Vec2(x, y))
                    '@' -> Robot(Vec2(x, y))
                    else -> null
                }
            }
        }.flatten()

        private val maxX = items.map { it.pos }.maxOf { it.x }
        private val maxY = items.map { it.pos }.maxOf { it.y }

        private val robot = items.first { it is Robot }
        val map = items.associateBy { it.pos }.toMutableMap()

        private fun moveableItems(pos: Vec2, dir: Vec2, maybeMovable: List<Item>): List<Item> {
            val newPos = pos + dir

            if (map.containsKey(newPos)) {
                val nextItem = map[newPos]!!

                // if we hit a wall, nothing is going anywhere
                if (nextItem is Wall) return emptyList()

                // if we hit a box, whether we can move the box depends on what's behind it
                if (nextItem is Box) return moveableItems(newPos, dir, maybeMovable + nextItem)
            }

            // otherwise the next spot is free space, so the item(s) identified so far can move, but nothing else
            return maybeMovable
        }

        fun makeMove(dir: Vec2) {
            val initialPos = robot.pos

            val moveableItems = moveableItems(initialPos, dir, listOf(robot))

            if (moveableItems.isNotEmpty()) {
                moveableItems.forEach { item ->
                    item.pos += dir
                    map[item.pos] = item
                }

                map.remove(initialPos)
            }
        }

        fun gps(): Int {
            return items.filterIsInstance<Box>().sumOf { it.pos.y * 100 + it.pos.x }
        }

        fun draw() {
            for (y in 0..maxY) {
                for (x in 0..maxX) {
                    val item = map[Vec2(x, y)]
                    when (item) {
                        null -> print(".")
                        is Wall -> {
                            print("#")
                        }

                        is Box -> {
                            print("O")
                        }

                        else -> {
                            print("@")
                        }
                    }
                }
                println("")
            }
        }
    }

    sealed class Item(var pos: Vec2) {
        abstract fun canMove(): Boolean
    }

    class Box(pos: Vec2) : Item(pos) {
        override fun canMove() = true
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

fun moveList(input: String): List<Vec2> {
    return input.mapNotNull { Vec2.move(it) }
}

fun part1(input: List<String>): Int {
    val warehouse = Warehouse(input.first())
    val moveList = moveList(input.last())

    moveList.forEach { warehouse.makeMove(it) }

    warehouse.draw()
    return warehouse.gps()
}

fun main() {
    val testInput = readAsBlocks("Day15_test")
    check(part1(testInput) == 10092)

    val input = readAsBlocks("Day15")
    part1(input).println()
}