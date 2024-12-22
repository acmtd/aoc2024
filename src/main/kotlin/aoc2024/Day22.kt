package aoc2024

fun main() {
    fun Long.prune() = this % 16777216
    fun Long.mix(other: Long) = this.xor(other)
    fun Long.prices() = this % 10

    fun Long.evolve(): Long {
        val step1 = this.mix(this * 64).prune()
        val step2 = step1.mix((step1 / 32)).prune()
        return step2.mix(step2 * 2048).prune()
    }

    fun secretNumbers(number: Long): List<Long> {
        var num = number

        return buildList {
            add(num)

            repeat(2000) {
                num = num.evolve()
                add(num)
            }
        }
    }

    fun part1(input: List<String>): Long {
        var numbers = input.map { it.toLong() }

        repeat(2000) {
            numbers = numbers.map { it.evolve() }
        }

        return numbers.sum()
    }

    fun part2(input: List<String>): Long {
        val numbers = input.map { it.toLong() }

        val priceMaps = numbers.map { number ->
            val secretNumbers = secretNumbers(number)
            val priceChanges = secretNumbers.map { it.prices() }.zipWithNext()
                .map { (oldPrice, newPrice) -> Pair(newPrice, newPrice - oldPrice) }

            buildMap {
                priceChanges.windowed(4)
                    .forEach { pairs ->
                        val changes = pairs.map { pair -> pair.second }
                        val price = pairs.last().first

                        putIfAbsent(changes, price)
                    }
            }
        }

        val allSequences = priceMaps.flatMap { it.keys }.distinct()

        val masterPriceMap = buildMap {
            allSequences.forEach { sequence ->
                put(sequence, priceMaps.sumOf { it.getOrDefault(sequence, 0L) })
            }
        }

        return masterPriceMap.maxOf { it.value }
    }

    val testInput1 = readAsLines("Day22_test")
    check(part1(testInput1) == 37327623L)

    val testInput2 = readAsLines("Day22_test2")
    check(part2(testInput2) == 23L)

    val input = readAsLines("Day22")
    part1(input).println()
    part2(input).println()
}

