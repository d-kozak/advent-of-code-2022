package day3

import java.io.FileInputStream

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day3/${fileName}"))

fun main() {
    setIn("input.txt")

    var sum1 = 0L
    var sum2 = 0L
    var line = readlnOrNull()
    val buffer = mutableListOf<Set<Char>>()
    while (line != null) {
        val len = line.length
        val left = line.substring(0, len / 2).toSet()
        val right = line.substring(len / 2).toSet()
        val common = left.intersect(right)
        require(common.size == 1) { "Expected size is 1, got $common" }
        val sharedElem = common.elementAt(0)
        val score = scoreFor(sharedElem)
        sum1 += score
        println("Shared elem: $sharedElem, score : $score ")


        buffer.add(line.toSet())
        if (buffer.size == 3) {
            val commonGroupItems = buffer[0] intersect buffer[1] intersect buffer[2]
            require(commonGroupItems.size == 1) { "Expected size is 1, got $commonGroupItems" }
            val sharedGroupItem = commonGroupItems.elementAt(0)
            val groupScore = scoreFor(sharedGroupItem)
            println("shared group item: $sharedGroupItem, score: $groupScore")
            sum2 += groupScore
            buffer.clear()
        }

        line = readlnOrNull()
    }
    println(sum1)
    println(sum2)
}

fun scoreFor(sharedElem: Char): Int = when {
    sharedElem in 'a'..'z' -> sharedElem - 'a' + 1
    sharedElem in 'A'..'Z' -> sharedElem - 'A' + 27
    else -> error("Bad shared elem type ${sharedElem}")
}
