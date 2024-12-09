package aoc2024

import kotlin.math.min

sealed interface Content {
    val length: Int
}

class Block(val id: Int, override val length: Int) : Content {
}

class FreeSpace(override val length: Int) : Content {
}

fun main() {
    fun parse(input: String): List<Content> {
        val blocks = input.indices.filter { it % 2 == 0 }
            .map { Block(it / 2, input[it].digitToInt()) }

        val freeSpaceSizes = input.indices.filter { it % 2 == 1 }
            .map { input[it].digitToInt() }
            .toMutableList().apply { add(0) }
            .map { FreeSpace(it) }

        return buildList {
            blocks.zip(freeSpaceSizes).forEach { (block, freeSpace) ->
                add(block)
                add(freeSpace)
            }
        }
    }

    tailrec fun compress(diskMap: List<Content>): List<Content> {
        val lastBlockPosition = diskMap.indices.last { diskMap[it] is Block }
        val firstFreeSpacePosition = diskMap.indices.first { diskMap[it] is FreeSpace }

        if (firstFreeSpacePosition > lastBlockPosition) return diskMap

        val block = diskMap[lastBlockPosition] as Block
        val freeSpace = diskMap[firstFreeSpacePosition] as FreeSpace

        // move part or all of the last block to replace the free space
        val amountToMove = min(freeSpace.length, block.length)

        val freeSpaceRemaining = freeSpace.length - amountToMove
        val blockRemaining = block.length - amountToMove

        val newDiskMap = buildList {
            addAll(diskMap.subList(0, firstFreeSpacePosition))
            add(Block(block.id, amountToMove))

            if (freeSpaceRemaining > 0) {
                add(FreeSpace(freeSpaceRemaining))
            }

            addAll(diskMap.subList(firstFreeSpacePosition + 1, lastBlockPosition))

            if (blockRemaining > 0) {
                add(Block(block.id, blockRemaining))
            }

            addAll(diskMap.subList(lastBlockPosition + 1, diskMap.size))
        }

        return compress(newDiskMap)
    }

    fun checksum(diskMap: List<Content>): Long {
        return diskMap.flatMap { c ->
            buildList {
                if (c is Block) {
                    repeat(c.length) { add(c.id.toLong()) }
                } else if (c is FreeSpace) {
                    repeat(c.length) { add(0.toLong()) }
                }
            }
        }.mapIndexed { index, id -> index * id }.sum()
    }

    fun part1(input: String): Long {
        return checksum(compress(parse(input)))
    }

    fun part2(input: String): Long {
        val initialDiskMap = parse(input)

        val lastBlockId = (initialDiskMap.last { it is Block } as Block).id

        var diskMap = initialDiskMap

        (lastBlockId.downTo(0)).forEach { id ->
            val blockPosition = diskMap.indices.last { diskMap[it] is Block && (diskMap[it] as Block).id == id }
            val block = diskMap[blockPosition] as Block

            // find the first block of free space that can fit the file
            val freeSpacePosition =
                diskMap.indices.firstOrNull { diskMap[it] is FreeSpace && diskMap[it].length >= block.length && it < blockPosition }

            if (freeSpacePosition != null) {
                val freeSpace = diskMap[freeSpacePosition] as FreeSpace
                val freeSpaceRemaining = freeSpace.length - block.length

                diskMap = buildList {
                    addAll(diskMap.subList(0, freeSpacePosition))
                    add(block)
                    if (freeSpaceRemaining > 0) {
                        add(FreeSpace(freeSpaceRemaining))
                    }
                    addAll(diskMap.subList(freeSpacePosition + 1, blockPosition))
                    add(FreeSpace(block.length))
                    addAll(diskMap.subList(blockPosition + 1, diskMap.size))
                }
            }
        }

        return checksum(diskMap)
    }

    val testInput = "2333133121414131402"
    check(part1(testInput) == 1928.toLong())
    check(part2(testInput) == 2858.toLong())

    val input = readAsString("Day09")
    part1(input).println()
    part2(input).println()
}

