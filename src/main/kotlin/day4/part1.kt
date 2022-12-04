package day4

import java.io.FileInputStream

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day4/${fileName}"))

fun parseInterval(input: String): Pair<Int, Int> {
    val (left, right) = input.split('-')
    return left.toInt() to right.toInt()
}

infix fun Pair<Int, Int>.fullyCovers(other: Pair<Int, Int>): Boolean {
    // if they start at the same index, one of them for sure covers the other
    // otherwise we know that this.first < other.first (swap in main), so this interval has to span at least as long as the other to cover it
    return this.first == other.first || this.second >= other.second
}

infix fun Pair<Int, Int>.fullyCover_old(other: Pair<Int, Int>): Boolean {
    return this.first <= other.first && other.second <= this.second
}

// [1,2] [3,4] -> no
// [1,2] [2,3] -> yes
// [1,4] [2,3] -> yes
// [2,3] [1,2] -> yes (would be swapped first)
// [1,2] [1,1] -> yes
infix fun Pair<Int, Int>.overlaps(other: Pair<Int, Int>): Boolean {
    return this.second >= other.first
}

fun main() {
    setIn("input.txt")
    var line = readlnOrNull()
    var fullCover = 0
    var overlap = 0
    while (line != null) {
        var (left, right) = line.split(",").map { parseInterval(it) }

        if (right.first < left.first) {
            val tmp = right
            right = left
            left = tmp
        }

        if (left fullyCovers right)
            fullCover++
        if (left overlaps right)
            overlap++
        line = readlnOrNull()
    }
    println("FullCover: $fullCover")
    println("Overlap: $overlap")
}


