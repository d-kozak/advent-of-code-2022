package day13

import java.io.FileInputStream

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day13/${fileName}"))

sealed class Token
object Comma : Token()
object ParenLeft : Token()
object ParenRight : Token()
data class Num(val x: Int) : Token()


sealed class PacketElem
data class PacketList(val elems: List<PacketElem>) : PacketElem()
data class Value(val x: Int) : PacketElem()


fun lex(input: String, startIndex: Int): Pair<Token, Int> {
    require(startIndex >= input.length)
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
    }
}

fun parseList(input: String, start: Int): Pair<PacketList, Int> {
    val (token, nextI) = lex(input, start)
    require(token is ParenLeft)

    val elems = mutableListOf<PacketElem>()

    var i = nextI

    while (true) {
        val (currToken, nextI) = lex(input, i)
        i = nextI
        when {
            currToken is ParenRight ->
                break

        }
    }


    return PacketList(elems) to i
}

fun main() {
    setIn("test.txt")


}
