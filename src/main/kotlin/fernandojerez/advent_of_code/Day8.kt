package fernandojerez.advent_of_code

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong

val factors = listOf(1000, 100, 10, 1)

fun String.checkPatternIn(pattern: String, occurrences: Int, result: Int): Int? {
    return result.takeIf {
        pattern.asSequence()
            .mapNotNull { c ->
                1.takeIf { this.contains(c) }
            }.sum() == occurrences
    }
}

fun <T> Sequence<T>.consume(): Sequence<T> {
    return this.toList().asSequence()
}

fun Sequence<Pair<Int, String>>.collect(into: MutableMap<Int, String>): MutableMap<Int, String> {
    return this.fold(into) { acc, (number, sequence) ->
        acc[number] = sequence
        acc
    }
}

fun Sequence<String>.toNumber(patterns: Map<String, Int>): Int {
    return this.mapIndexed { index, pattern ->
        val result = patterns.asSequence().filter { (p, _) ->
            (p.length == pattern.length) && (
                    pattern.asSequence()
                        .filter { c -> !p.contains(c) }
                        .count() == 0)
        }.first().value
        result * factors[index]
    }.sum()
}

fun String.missing(pattern: String): Char? {
    return this.asSequence().filter {
        !pattern.contains(it)
    }.firstOrNull()
}

fun Sequence<String>.calculatePatterns(): Map<String, Int> {
    val decode = this.mapNotNull {
        when (it.length) {
            2 -> 1 to it
            4 -> 4 to it
            3 -> 7 to it
            7 -> 8 to it
            else -> null
        }
    }.collect(mutableMapOf())

    this.filter { it.length == 6 }
        .map {
            (decode[4]?.checkPatternIn(it, 4, 9)
                ?: decode[7]?.checkPatternIn(it, 3, 0)
                ?: 6) to it
        }.collect(decode)

    this.filter { it.length == 5 }
        .map {
            (decode[7]?.checkPatternIn(it, 3, 3)
                ?: 2.takeIf { _ -> it.missing(decode[9]!!)?.let { c -> !decode[1]!!.contains(c) } ?: false }
                ?: 5) to it
        }.collect(decode)
    
    val result = mutableMapOf<String, Int>()
    decode.forEach { (key, value) -> result[value] = key }
    return result.toMap()
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day08.txt").readText().trim()
    val validDigitSegmentLength = setOf(2, 4, 3, 7)

    //part 1
    input.splitToSequence("\n")
        .map { it.splitToSequence("|").last().trim() }
        .map { it.splitToSequence(" ") }
        .flatten()
        .filter { validDigitSegmentLength.contains(it.length) }
        .count()
        .also { println(it) }

    runBlocking {
        val result = AtomicLong(0L)
        input.splitToSequence("\n")
            .map {
                val parts = it.splitToSequence("|")
                val patterns = parts.first().trim().splitToSequence(" ").consume()
                val number = parts.last().trim().splitToSequence(" ")
                number to patterns
            }.asFlow()
            .buffer()
            .map { (number, patterns) -> number.toNumber(patterns.calculatePatterns()) }
            .collect {
                result.addAndGet(it.toLong())
            }
        println(result)
    }
}
