package fernandojerez.advent_of_code

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong


fun List<Pair<Int, List<Int>>>.isLowerValueUpDown(line: Int, index: Int, numLines: Int, value: Int): Boolean {
    return when (line) {
        0 -> value < this[1].second[index]
        numLines - 1 -> value < this[line - 1].second[index]
        else -> value < this[line - 1].second[index] && value < this[line + 1].second[index]
    }
}

fun List<Pair<Int, List<Int>>>.isLowerValue(line: Int, index: Int, value: Int): Boolean {
    val size = this[0].second.size
    val numLines = this.size
    return when (index) {
        0 -> this.isLowerValueUpDown(line, index, numLines, value) && (value < this[line].second[1])
        size - 1 -> this.isLowerValueUpDown(line, index, numLines, value) && (value < this[line].second[index - 1])
        else -> this.isLowerValueUpDown(line, index, numLines, value) &&
                (value < this[line].second[index - 1]) &&
                (value < this[line].second[index + 1])
    }
}

fun List<Pair<Int, List<Int>>>.buildBasin(line: Int, index: Int, elements: MutableSet<Pair<Int, Int>>) {
    if (elements.contains(line to index)) return
    if (line == -1 || index == -1) return
    if (line >= this.size) return
    if (index >= this[0].second.size) return
    if (this[line].second[index] == 9) return

    elements.add(line to index)
    buildBasin(line, index - 1, elements)
    buildBasin(line, index + 1, elements)
    buildBasin(line - 1, index, elements)
    buildBasin(line + 1, index, elements)
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day09.txt").readText().trim()
    val lines = input.splitToSequence("\n")
        .mapIndexed { index, line ->
            index to line.asSequence().map { c ->
                c.toString().toInt()
            }.toList()
        }
        .toList()


    runBlocking {
        val result = AtomicLong(0)
        val basins = mutableListOf<Int>()
        lines.asFlow()
            //.buffer()
            .map { (line, values) ->
                values.asSequence().mapIndexedNotNull { index, item ->
                    item.takeIf { _ ->
                        lines.isLowerValue(line, index, item)
                    }?.let {
                        val basin = mutableSetOf<Pair<Int, Int>>()
                        lines.buildBasin(line, index, basin)
                        basin.size to it.inc()
                    }
                }.toList().asSequence()
            }.collect {
                val sum = it.map { v -> v.second }.sum()
                it.map { v -> v.first }.forEach { b ->
                    basins.add(b)
                }
                result.addAndGet(sum.toLong())
            }
        basins.sort()
        val product = basins.asReversed().asSequence().take(3)
            .fold(1) { acc, v ->
                acc * v
            }
        println(product)
        println(result.get())
    }


}

