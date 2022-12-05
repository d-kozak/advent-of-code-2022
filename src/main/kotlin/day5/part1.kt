package day5

import java.io.FileInputStream

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day5/${fileName}"))

fun main() {
//    setIn("test.txt")
    setIn("input.txt")
    val crates = parseCrates()
    for (crate in crates) {
        println(crate)
    }
    println("===")
    executeOrders(crates)
    println("===")
    for (crate in crates) {
        println(crate)
    }
    println(crates.map { it.last() }.joinToString(separator = ""))
}

fun executeOrders(crates: List<MutableList<Char>>) {

    val regex = "^.*?(\\d+).*?(\\d+).*?(\\d)$".toRegex()

    var line = readlnOrNull()
    while (line != null) {
        val res = regex.find(line)
        val (_, cnt, from, to) = res!!.groups.stream().map { it?.value?.toIntOrNull() }.toList()
        println("$cnt $from $to")
        for (crate in crates) println(crate)


//        part1Stacks(cnt!!, crates, from!!, to!!)
        part2MoveMultiple(cnt!!, crates, from!!, to!!)

        line = readlnOrNull()
    }
}

fun part2MoveMultiple(cnt: Int, crates: List<MutableList<Char>>, from: Int, to: Int) {
    for (i in cnt downTo 1) {
        val c = crates[from - 1][crates[from - 1].size - i]
        crates[to - 1].add(c)
    }
    repeat(cnt) { crates[from - 1].removeLast() }
}

private fun part1Stacks(
    cnt: Int,
    crates: List<MutableList<Char>>,
    from: Int,
    to: Int
) {
    repeat(cnt) {
        val x = crates[from - 1].removeLast()
        crates[to - 1].add(x)
    }
}

fun parseCrates(): List<MutableList<Char>> {
    val stacks = mutableListOf<MutableList<Char>>()
    var line = readln()
    while (line.isNotEmpty()) {
        for (i in 0 until line.length) {
            val j = i * 4
            if (j >= line.length) break
            val c = line[j + 1]
            if (c.isLetter()) {
                while (i >= stacks.size)
                    stacks.add(mutableListOf())
                stacks[i].add(c)
            }
        }


        line = readln()
    }

    for (stack in stacks) {
        stack.reverse()
    }

    return stacks
}
