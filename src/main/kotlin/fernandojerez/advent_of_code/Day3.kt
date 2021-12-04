package fernandojerez.advent_of_code

data class Bit(val zero: Int = 0, val one: Int = 0)

data class Result(
    val gamma: StringBuilder = StringBuilder(),
    val epsilon: StringBuilder = StringBuilder()
)

data class OxygenCo2Result(
    val index: Int = 0,
    val oxygen: Sequence<Sequence<Bit>> = sequenceOf(),
    val co2: Sequence<Sequence<Bit>> = sequenceOf(),
)

fun String.convertToBits(): Sequence<Bit> {
    return this.asSequence()
        .map {
            if (it == '1') Bit(one = 1)
            else Bit(zero = 1)
        }
}

fun Sequence<Bit>.bits2Int(): Int {
    return this.fold(StringBuilder()) { acc, it ->
        acc.append(if (it.one > it.zero) "1" else "0")
    }.toString().toInt(2)
}

fun Sequence<Sequence<Bit>>.summarize(index: Int?): Sequence<Bit> {
    return this.map {
        it.takeIf { index == null }
            ?: sequenceOf(it.elementAt(index!!))
    }.reduce { accSeq, items ->
        accSeq.zip(items) { acc, item ->
            Bit(
                zero = acc.zero + item.zero,
                one = acc.one + item.one
            )
        }
    }
}

fun Sequence<Sequence<Bit>>.filterByMask(index: Int, mask: (bit: Bit) -> Bit): Sequence<Sequence<Bit>> {
    if (this.count() == 1) {
        return this
    }
    return this.summarize(index).map(mask).first().let { bit ->
        this.filter {
            it.elementAt(index) == bit
        }
    }
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day03.txt").readText().trim()
    val bits = input.splitToSequence("\n")
        .map { it.convertToBits() }

    bits.summarize(null).fold(Result()) { result, item ->
        if (item.one > item.zero) {
            result.gamma.append("1")
            result.epsilon.append("0")
        } else {
            result.gamma.append("0")
            result.epsilon.append("1")
        }
        result
    }.also {
        val g = it.gamma.toString().toInt(2)
        val e = it.epsilon.toString().toInt(2)
        println(g * e)
    }

    val numberOfBits = bits.first().count()
    generateSequence(OxygenCo2Result(oxygen = bits, co2 = bits)) {
        if (it.index >= numberOfBits) return@generateSequence null
        OxygenCo2Result(
            index = it.index.inc(),
            oxygen = it.oxygen.filterByMask(it.index) { bit ->
                if (bit.zero > bit.one) Bit(zero = 1)
                else Bit(one = 1)
            },
            co2 = it.co2.filterByMask(it.index) { bit ->
                if (bit.zero > bit.one) Bit(one = 1)
                else Bit(zero = 1)
            }
        )
    }.last().also {
        val oxygen = it.oxygen.first().bits2Int()
        val co2 = it.co2.first().bits2Int()
        println(oxygen * co2)
    }
}
