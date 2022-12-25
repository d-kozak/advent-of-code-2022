package day25

import java.io.FileInputStream
import kotlin.math.abs
import kotlin.system.exitProcess

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day25/${fileName}"))

fun processSnafu(line: String): Long {
    var d = 1L
    val n = line.length
    var res = 0L
    for (i in n - 1 downTo 0) {
        val x = when (line[i]) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> error("Bad op ${line[i]}")
        }
        res += x * d
        d *= 5
    }
    return res
}

fun toSnafu(decimal: Long): String {
    val prefixSum = LongArray(30)
    prefixSum[0] = 1
    var d = 5L
    for (i in 1 until 30) {
        prefixSum[i] = prefixSum[i - 1]
        prefixSum[i] += 2 * d
        d *= 5
    }

    d = 1
    var i = 1
    while (d < decimal) {
        i++
        d *= 5
    }

    val moves = listOf(
        2 to '2',
        1 to '1',
        0 to '0',
        -1 to '-',
        -2 to '='
    )


    data class State(val rem: Long, val d: Long)

//    val seen = mutableSetOf<State>()

    fun dfs(rem: Long, d: Long, i: Int, used: String) {
//        val state = State(rem, d)
//        if (!seen.add(state)) {
//            println(".")
//            return
//        }
//        println(state)
        if (d == 0L && rem == 0L) {
            println("Part 1")
            println(used)
            check(processSnafu(used) == decimal)
            return
        }
        if (d == 0L) return
        if (abs(rem) > prefixSum[i]) {
//            println(".")
            return
        }

        for ((diff, c) in moves) {
            dfs(rem - diff * d, d / 5, i - 1, used + c)
        }
    }

    dfs(decimal, d, i, "")

    exitProcess(42)

    /*

    val res = StringBuilder()

    var rem = decimal
    while (rem != 0L) {
        if (rem > 0) {
            for (x in 2 downTo 0) {
                val diff = rem - x * d
                if (prefixSum[i - 1] + diff > 0) {
                    res.append((x.toChar() + '0'.code))
                    rem -= x * d
                    break
                }
            }
        } else if (rem < 0) {
            for (x in 2 downTo 0) {
                val diff = (-rem) - x * d
                if (prefixSum[i - 1] - diff > 0) {
                    res.append(
                        when (x) {
                            2 -> '='
                            1 -> '-'
                            0 -> '0'
                            else -> error("bad x $x")
                        }
                    )
                    rem += x * d
                    break
                }
            }
        } else {
            res.append('0')
        }
        i--
        d /= 5
    }

    return res.toString()
     */
}

// 2, 1, 0, minus (written -), and double-minus (written =)
fun main() {
    setIn("input.txt")

    var sum = 0L

    var line = readlnOrNull()
    while (line != null) {
        val x = processSnafu(line)
        println("$line = $x")
        sum += x
        line = readlnOrNull()
    }


    println("Part 1")
    println(sum)
    println(toSnafu(sum))
}
