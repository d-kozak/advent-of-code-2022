package day10

import java.io.FileInputStream
import kotlin.math.abs

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day10/${fileName}"))

fun main() {
    setIn("input.txt")

    var cmd = readlnOrNull()
    var cycle = 1
    var nextCycle = 60
    var x = 1
    var signal = 0
    var position = 0
    while (cmd != null) {
        if (abs(x - position) <= 1) print('#')
        else print('.')
        if (position == 39) {
            position = 0
            println()
        } else position++

        val isnoop = "noop" == cmd
        var add = 0
        if (!isnoop) {
            add = cmd.split(' ')[1].toInt()
        }
        if (cycle == 20 || cycle == nextCycle) {
//            println(cycle * x)
            signal += cycle * x
            if (cycle != 20)
                nextCycle += 40
        }
        if (!isnoop) {
            cycle++
            if (abs(x - position) <= 1) print('#')
            else print('.')
            if (position == 39) {
                position = 0
                println()
            } else position++
            if (cycle == 20 || cycle == nextCycle) {
//                println(cycle * x)
                signal += cycle * x
                if (cycle != 20)
                    nextCycle += 40
            }
            x += add
        }
        cycle++
        cmd = readlnOrNull()
    }

    println(signal)
}