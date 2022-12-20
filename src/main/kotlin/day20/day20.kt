package day20

import java.io.FileInputStream


fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day20/${fileName}"))
fun main() {
    setIn("input.txt")
    val nums = mutableListOf<Int>()
    var line = readlnOrNull()
    while (line != null) {
        nums.add(line.toInt())
        line = readlnOrNull()
    }

    val indexed = nums.withIndex().toMutableList()
    val n = indexed.size

    fun mod(i: Int): Int {
        var x = i % n
        if (x < 0) x += n
        return x
    }

//    println(indexed.map { it.value })

    for (i in 0 until n) {
        val pos = indexed.indexOfFirst { it.index == i }
//        println("${indexed[pos]}")
        when {
            indexed[pos].value > 0 -> {
                val lo = pos
                val hi = pos + indexed[pos].value

                val orig = indexed[lo]
                for (i in lo + 1..hi) {
                    indexed[mod(i - 1)] = indexed[mod(i)]
                }
                indexed[mod(hi)] = orig
            }

            indexed[pos].value < 0 -> {
                val hi = pos
                val lo = pos + indexed[pos].value

                val orig = indexed[hi]
                for (i in hi downTo lo + 1) {
                    indexed[mod(i)] = indexed[mod(i - 1)]
                }
                indexed[mod(lo)] = orig
            }

            else -> {
                // no op
            }
        }

//        println(indexed.map { it.value })
    }
    val zeroIndex = indexed.indexOfFirst { it.value == 0 }
    val indices = listOf(zeroIndex + 1000, zeroIndex + 2000, zeroIndex + 3000)
        .map { mod(it) }
        .map { indexed[it].value }
    println(indices)
    println(indices.sum())
}