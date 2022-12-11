package day11

import java.io.FileInputStream

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day11/${fileName}"))

data class Item(var worryLevel: Long) {
    override fun toString(): String {
        return worryLevel.toString()
    }
}

data class Monkey(
    var items: ArrayDeque<Item> = ArrayDeque(),
    var operation: (Long) -> Long,
    var test: (Item) -> Boolean,
    var trueIndex: Int,
    var falseIndex: Int,
    val div: Long
)

fun readMonkey(): Monkey? {
    // skip the first line
    if (readlnOrNull() == null)
        return null // no more data to read
    val items = readItems()
    val op = readOperation()
    val (div, test) = readTest()
    val trueBranch = readLastInt()
    val falseBranch = readLastInt()
    readlnOrNull() // read empty line
    return Monkey(ArrayDeque(items), op, test, trueBranch, falseBranch, div)
}

fun readTest(): Pair<Long, (Item) -> Boolean> {
    val div = readLastInt().toLong()
    return div to { x -> x.worryLevel % div == 0L }
}

private fun readLastInt(): Int {
    val line = readln()
    var i = line.length - 1
    while (line[i - 1].isDigit())
        i--
    val div = line.substring(i).toInt()
    return div
}

fun readOperation(): (Long) -> Long {
    val line = readln()
    var i = line.lastIndexOf('+')
    if (i != -1) {
        val other = line.substring(i + 2)
        if (other == "old") return { x -> x + x }
        else {
            val value = other.toLong()
            return { x -> x + value }
        }
    }
    i = line.lastIndexOf('*')
    if (i != -1) {
        val other = line.substring(i + 2)
        if (other == "old") return { x -> x * x }
        else {
            val value = other.toLong()
            return { x -> x * value }
        }
    }
    error("Unknown operation at $line")
}

fun readItems(): List<Item> {
    val line = readln()
    val i = line.indexOf(':')
    return line.substring(i + 1)
        .split(',')
        .map { it.trim() }
        .map { it.toLong() }
        .map { Item(it) }
}

fun readMonkeys(): List<Monkey> {
    val res = mutableListOf<Monkey>()
    var monkey = readMonkey()
    while (monkey != null) {
        res.add(monkey)
        monkey = readMonkey()
    }
    return res
}

fun main() {
    setIn("input.txt")
    val monkeys = readMonkeys()
    for (monkey in monkeys)
        println(monkey)
    val div = monkeys.map { it.div }.reduce { l, r -> l * r }
    println(div)
    println("===")
    val moves = LongArray(monkeys.size)
    var cnt = 0
    // 20 for part 1
    for (step in listOf(1, 19, 980, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000)) {
        repeat(step) {
            for ((i, monkey) in monkeys.withIndex()) {
                while (monkey.items.isNotEmpty()) {
                    val curr = monkey.items.removeFirst()
//                    curr.worryLevel = monkey.operation(curr.worryLevel % 1_000_000)
                    curr.worryLevel = monkey.operation(curr.worryLevel)
                    // div 3 only for part 1
//                     curr.worryLevel = (0.31 * curr.worryLevel).toLong()
//                    curr.worryLevel = min(curr.worryLevel,1_000_000)
//                    curr.worryLevel %= 100_000
//                    curr.worryLevel = sqrt(curr.worryLevel.toDouble()).toLong()
                    curr.worryLevel %= div
                    if (monkey.test(curr))
                        monkeys[monkey.trueIndex].items.addLast(curr)
                    else
                        monkeys[monkey.falseIndex].items.addLast(curr)
                    moves[i]++
                }
            }
        }
        cnt += step
        println("step $cnt: ")
        for (monkey in monkeys)
            println(monkey)
        println(moves.contentToString())
        println("===")
    }

    var best = -1L
    var secondBest = -1L
    for (move in moves) {
        if (move > best) {
            secondBest = best
            best = move
        } else if (move > secondBest) {
            secondBest = move
        }
    }

    println("$best * $secondBest = ${best * secondBest}")
}
