package day20.v2

import java.io.FileInputStream

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day20/${fileName}"))

const val KEY = 811_589_153L

private fun parseNums(): MutableList<Long> {
    var line = readlnOrNull()
    val nums = mutableListOf<Long>()
    while (line != null) {
        nums.add(line.toLong())
        line = readlnOrNull()
    }
    return nums
}

fun solve(nums: MutableList<Long>, part: Int) {
    val n = nums.size
    val queue = ArrayDeque<IndexedValue<Long>>()

    for (i in nums.indices) {
        val x = if (part == 2) nums[i] * KEY else nums[i]
        queue.add(IndexedValue(i, x))
    }

//    println(queue.map { it.value })

    val iter = if (part == 2) 10 else 1
    repeat(iter) {
        println(it)

        for (i in 0 until queue.size) {
            while (queue.first().index != i) {
                queue.addLast(queue.removeFirst())
            }

            val elem = queue.removeFirst()
            var move = (elem.value % queue.size).toInt()
            if (move < 0) move += queue.size

            repeat(move) {
                queue.addLast(queue.removeFirst())
            }

            queue.addLast(elem)
//            println(queue.map { it.value })
        }
    }



    println(queue.map { it.value })
    val zeroPos = queue.indexOfFirst { it.value == 0L }
    val elems = listOf(1, 2, 3)
        .map { it * 1000 }
        .map { it + zeroPos }
        .map { it % n }
        .map { queue[it] }
        .map { it.value }

    println(elems)
    println(elems.sum())
}

fun main() {
    setIn("input.txt")
    val nums = parseNums()
    println("part 1")
    solve(nums, 1)
    println("part 2")
    solve(nums, 2)
}
