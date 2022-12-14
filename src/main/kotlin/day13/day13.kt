package day13

import java.io.FileInputStream
import kotlin.math.min

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day13/${fileName}"))

sealed class Token
object Comma : Token()
object ParenLeft : Token()
object ParenRight : Token()
data class Num(val x: Int) : Token()


sealed class PacketElem : Comparable<PacketElem> {
    override fun compareTo(other: PacketElem): Int = compare(this, other)
}

data class PacketList(val elems: List<PacketElem>) : PacketElem() {
    override fun toString(): String {
        return elems.joinToString(",", prefix = "[", postfix = "]")
    }
}

data class Value(val x: Int) : PacketElem() {
    override fun toString(): String {
        return x.toString()
    }
}


fun lex(input: String, startIndex: Int): Pair<Token, Int> {
    require(startIndex < input.length)
    return when {
        input[startIndex] == '[' -> ParenLeft to startIndex + 1
        input[startIndex] == ']' -> ParenRight to startIndex + 1
        input[startIndex] == ',' -> Comma to startIndex + 1
        input[startIndex].isDigit() -> {
            var end = startIndex
            while (end < input.length && input[end].isDigit())
                end++
            val x = input.substring(startIndex, end).toInt()
            Num(x) to end
        }

        else -> error("Unknown symbol '${input[startIndex]}' at $startIndex in $input")
    }
}

fun parse(input: String, start: Int): Pair<PacketElem, Int> {
    val (token, nextI) = lex(input, start)
    return when {
        token is ParenLeft -> parseList(input, start)
        token is Num -> Value(token.x) to nextI
        else -> error("Bad token $token at ${input[start]}")
    }
}

fun parseList(input: String, start: Int): Pair<PacketList, Int> {
    val (token, _) = lex(input, start)
    require(token is ParenLeft)

    val elems = mutableListOf<PacketElem>()

    var i = start + 1

    while (true) {
        if (lex(input, i).first is ParenRight) {
            i++
            break
        }
        val (elem, nextI) = parse(input, i)
        i = nextI
        elems.add(elem)
        val (nextToken, _) = lex(input, i)
        when {
            nextToken is Comma -> {
                i++
            }

            nextToken is ParenRight -> {
                i++
                break
            }
        }
    }

    return PacketList(elems) to i
}

fun compare(left: PacketElem, right: PacketElem): Int = when {
    left is Value && right is Value -> left.x.compareTo(right.x)
    left is PacketList && right is PacketList -> comparePacketLists(left, right)
    left is PacketList && right is Value -> comparePacketLists(left, PacketList(listOf(right)))
    left is Value && right is PacketList -> comparePacketLists(PacketList(listOf(left)), right)
    else -> error("Should be unreachable")
}

fun comparePacketLists(left: PacketList, right: PacketList): Int {
    for (i in 0 until min(left.elems.size, right.elems.size)) {
        val res = compare(left.elems[i], right.elems[i])
        if (res != 0)
            return res
    }
    return when {
        left.elems.size < right.elems.size -> -1
        left.elems.size > right.elems.size -> 1
        else -> 0
    }
}


fun main() {
    setIn("input.txt")

    println(parse("[]", 0))

    var curr = readlnOrNull()
    var i = 1
    var sum = 0

    val all = mutableListOf<PacketElem>()

    while (curr != null) {
        val left = parse(curr, 0).first
        val right = parse(readln(), 0).first

        println(left)
        println(right)
        all.add(left)
        all.add(right)

        if (compare(left, right) < 0) {
            println("sorted $i")
            sum += i
        }

        println("===")


        curr = readlnOrNull()
        if (curr != null) {
            // skip empty line
            curr = readlnOrNull()
        }
        i++
    }
    println(sum)


    println("===part 2===")

    val firstDelim = parse("[[2]]", 0).first
    val secondDelim = parse("[[6]]", 0).first

    all.add(firstDelim)
    all.add(secondDelim)

    all.sort()

    var fst = -1
    var snd = -1

    for ((i, p) in all.withIndex()) {
        println(p)
        if (p == firstDelim)
            fst = i + 1
        if (p == secondDelim)
            snd = i + 1
    }

    println("$fst * $snd = ${fst * snd}")
}
