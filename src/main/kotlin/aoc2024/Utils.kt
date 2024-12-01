package aoc2024

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readAsString(name: String) = Path("src/main/resources/$name.txt").readText().trim()
fun readAsLines(name: String) = readAsString(name).lines()
fun readAsBlocks(name: String) = readAsString(name).split("\n\n")

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
