package fernandojerez.advent_of_code


fun Sequence<Int>.createSequences(slide: Int): Sequence<List<Int>> {
    val iterator = this.iterator()
    val firstGroup = (0 until slide).mapNotNull {
        if (iterator.hasNext()) iterator.next()
        else null
    }
    return generateSequence(firstGroup.takeIf { it.size == slide }) {
        if(iterator.hasNext()){
            it.drop(1) + iterator.next()
        } else null
    }
}

fun main(){
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day01.txt").readText()
    input.splitToSequence("\n")
        .mapNotNull { it.toIntOrNull() }
        .createSequences(3)
        .map { it.sum()  }
        .createSequences(2)
        .map {
            if(it[1] > it[0]) 1 else  0
        }
        .sum()
        .also { println(it) }
}
