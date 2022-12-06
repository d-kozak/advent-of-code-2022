package day6

import java.io.FileInputStream


fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day6/${fileName}"))

val patternSize = 14

fun processLine(line: String) {
    val elems = mutableMapOf<Char, Int>()
    for (i in line.indices) {
        if (i - patternSize >= 0) {
            val prev = elems[line[i - patternSize]]!!
            if (prev == 1) elems.remove(line[i - patternSize])
            else elems[line[i - patternSize]] = prev - 1
        }
        val c = line[i]
        elems[c] = elems.getOrDefault(c, 0) + 1
        if (elems.size == patternSize) {
            println("starting position: ${i + 1} , chars:$elems, input $line")
            return
        }
    }
    error("start of packet sequence not found in $line")
}

fun main() {
    setIn("input.txt")

    var line = readlnOrNull()
    while (line != null) {
        processLine(line)
        line = readlnOrNull()
    }
}

