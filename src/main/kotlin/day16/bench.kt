package day16

import kotlin.system.measureTimeMillis


fun doWork(i: Int) {
    val x = i + 1

    println("$x =====")
}

fun main() {

    val t = measureTimeMillis {
        for (i in 1..1_637_482_496) {
            doWork(i)
        }
    }

    println(t)
    println(t / 1000)
    println(t / (1000 * 60))
}