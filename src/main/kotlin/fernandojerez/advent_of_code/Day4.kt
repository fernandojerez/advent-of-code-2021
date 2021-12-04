package fernandojerez.advent_of_code

import java.util.concurrent.atomic.AtomicBoolean

data class Piece(val value: Int, val checked: Boolean = false)

data class Board(
    val pieces: Sequence<Piece> = sequenceOf(),
    val winner: Boolean = false,
    val lastValue: Int = 0
) {
    operator fun get(row: Int, column: Int): Piece {
        return this.pieces.elementAt(row * 5 + column)
    }
}

fun <T> Sequence<T>.compute(): Sequence<T> {
    return this.toList().asSequence()
}

fun List<String>.toBoard(): Board {
    return this.asSequence()
        .map {
            it.trim().splitToSequence("\\s".toRegex())
                .mapNotNull { piece -> piece.toIntOrNull() }
                .map { value -> Piece(value = value) }
                .compute()
        }.flatten().let {
            Board(pieces = it)
        }
}

fun Board.markPiece(value: Int): Pair<Boolean, Board> {
    val checked = AtomicBoolean(false)
    val newState = this.pieces.map {
        if (it.value != value) it
        else {
            checked.set(true)
            it.copy(checked = true)
        }
    }.compute()
    return checked.get() to Board(pieces = newState, winner = this.winner)
}

fun Board.getResult(): Int {
    return this.pieces.filter { !it.checked }.map { it.value }.sum() * this.lastValue
}

fun Board.checkIfIsWinner(value: Int): Board {
    val rowCompleted = (0 until 5).map { row ->
        (0 until 5).sumOf { col ->
            1.takeIf { this[row, col].checked } ?: 0
        }
    }.firstOrNull { it == 5 } != null
    val colCompleted = (0 until 5).map { col ->
        (0 until 5).sumOf { row ->
            1.takeIf { this[row, col].checked } ?: 0
        }
    }.firstOrNull { it == 5 } != null
    return Board(pieces = this.pieces, winner = rowCompleted or colCompleted, lastValue = value)
}

fun Sequence<Board>.apply(value: Int): Sequence<Board> {
    return this.map {
        val result = it.markPiece(value)
        if (result.first) result.second.checkIfIsWinner(value)
        else result.second
    }
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day04.txt").readText().trim()
    val seq = input.splitToSequence("\n")
    val selections = seq.first()
        .splitToSequence(",")
        .mapNotNull { it.toIntOrNull() }
        .iterator()

    val boards = seq.drop(2)
        .filter { it.isNotEmpty() }
        .chunked(5)
        .map { it.toBoard() }
        .compute()

    val result = generateSequence(boards) {
        if (!selections.hasNext()) return@generateSequence null
        val round = selections.next()
        it.filter { board -> !board.winner }.apply(round)
    }.drop(1)
        .flatten()
        .filter { it.winner }
        .compute()

    result.firstOrNull()?.let {
        println(it.getResult())
    }
    result.lastOrNull()?.let {
        println(it.getResult())
    }
}
