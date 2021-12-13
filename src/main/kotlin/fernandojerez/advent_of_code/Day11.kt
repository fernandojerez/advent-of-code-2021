package fernandojerez.advent_of_code


/**
-11  -10  -9
-1    0    1
+9   10   11
 */
val middleSteps = listOf<Int>(-9, -10, -11, -1, 1, 9, 10, 11).asSequence()
val leftCornerSteps = listOf<Int>(-10, -9, 1, 10, 11).asSequence()
val rightCornerSteps = listOf<Int>(-11, -10, -1, 9, 10).asSequence()
val chunkSize = 10

fun MutableList<Int>.increaseAdjacents(index: Int) {
    when {
        index % chunkSize == 0 -> leftCornerSteps
        (index + 1) % chunkSize == 0 -> rightCornerSteps
        else -> middleSteps
    }.forEach {
        this.increase(index + it)
    }
}

fun MutableList<Int>.increase(index: Int) {
    if (index >= 0 && index < this.size && this[index] != 0) {
        this.setValue(index, 1)
    }
}

fun MutableList<Int>.setValue(index: Int, value: Int) {
    this[index] += value
    if (this[index] >= 10) {
        this[index] = 0
        this.increaseAdjacents(index)
    }
}

fun Sequence<Int>.step(size: Int): Sequence<Int> {
    val newList = (0 until size).asSequence().map { 0 }.toMutableList()
    return this.map { it + 1 }
        .mapIndexed { index, value -> index to (0.takeIf { value == 10 } ?: value) }
        .also {
            it.forEach { (index, value) ->
                newList[index] = value
            }
        }
        .fold(newList) { list, (index, value) ->
            if (value == 0) {
                list.increaseAdjacents(index)
            }
            list
        }.asSequence()
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day11.txt").readText().trim()
    val octupus = input.splitToSequence("\n")
        .map { it.asSequence() }
        .flatten()
        .map {
            it.toString().toInt()
        }.consume()

    val size = octupus.count()
    generateSequence(octupus) {
        it.step(size)
    }.drop(1).map {
        it.filter { v -> v == 0 }.count()
    }
        .take(100).sum().also { println(it) }

    generateSequence(octupus) {
        it.step(size)
    }.drop(1).mapIndexed { index, it ->
        index to it.filter { v -> v == 0 }.count()
    }.first { it.second == 100 }
        .also { println(it.first + 1) }
}
