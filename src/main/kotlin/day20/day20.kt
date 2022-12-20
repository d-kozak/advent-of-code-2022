package day20

import java.io.FileInputStream


fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day20/${fileName}"))

val key = 811_589_153L


data class Obj(val index: Int, var value: Long, var origValue: Long)

fun main() {
    setIn("test.txt")
    val indexed = mutableListOf<Obj>()
    var i = 0
    var line = readlnOrNull()
    while (line != null) {
        val x = line.toLong()
        indexed.add(Obj(i++, x, x))
        line = readlnOrNull()
    }

    val n = indexed.size

    for (i in 0 until n) {
        indexed[i].origValue *= key
        indexed[i].value = ((key % n) * (indexed[i].value % n)) % n
    }

//    val indexed = nums.withIndex().toMutableList()


    fun mod(i: Long): Int {
        var x = (i % n).toInt()
        if (x < 0) x += n
        return x
    }

    println(indexed.map { it.origValue })
    println(indexed.map { it.value })


    repeat(10) {
        println("Iter $it")
        for (i in 0 until n) {
            val pos = indexed.indexOfFirst { it.index == i }
            require(pos >= 0) { "Did not find index $i in $indexed" }
//            println("${indexed[pos]}")
            when {
                indexed[pos].value > 0 -> {
                    val lo = pos
                    var hi = pos + indexed[pos].value

//                    val len = (hi - lo) % n
//                    require(len > 0)
////                    hi = lo + len
//                    var cnt = 0

                    val orig = indexed[lo]
                    for (i in lo + 1..hi) {
                        indexed[mod(i - 1)] = indexed[mod(i)]
//                        if (++cnt >= len) break
                    }
                    indexed[mod(hi)] = orig
                }

                indexed[pos].value < 0 -> {
                    val hi = pos
                    var lo = pos + indexed[pos].value

//                    val len = (hi - lo) % n
//                    require(len > 0)
////                    lo = hi - len
//                    var cnt = 0

                    val orig = indexed[hi]
                    for (i in hi downTo lo + 1) {
                        indexed[mod(i)] = indexed[mod(i - 1)]
//                        if (++cnt >= len) break
                    }
                    indexed[mod(lo)] = orig
                }

                else -> {
                    // no op
                }
            }

//            println(indexed.map { it.value })
        }
    }

//    println(indexed.map { it.value })

    val zeroIndex = indexed.indexOfFirst { it.value == 0L }
    val indices = listOf(zeroIndex + 1000L, zeroIndex + 2000L, zeroIndex + 3000L)
        .map { mod(it) }
        .map { indexed[it].origValue }
    println(indices)
    println(indices.sum())
}