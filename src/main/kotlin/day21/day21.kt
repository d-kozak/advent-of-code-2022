package day21

import java.io.FileInputStream


fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day21/${fileName}"))

sealed class Node {
    abstract val name: String
    var res: Long = -1

    var isHumn: Boolean = false
    var containsHumn: Boolean = false
}

data class Num(val x: Long, override val name: String) : Node() {
}

data class Op(val left: String, val op: Char, val right: String, override val name: String) : Node() {

}

fun parseNode(input: String): Node {
    val parts = input.split(' ')
    val name = parts[0].substring(0, parts[0].length - 1)
    return if (parts.size == 2) Num(parts[1].toLong(), name)
    else Op(parts[1], parts[2][0], parts[3], name)
}

fun main() {
    setIn("input.txt")

    val nodes = mutableMapOf<String, Node>()

    var line = readlnOrNull()
    while (line != null) {
        val node = parseNode(line)
        nodes[node.name] = node
        line = readlnOrNull()
    }

    for (node in nodes) {
        println(node)
    }

    fun eval(node: Node): Long {
        return when (node) {
            is Num -> {
                node.res = node.x
                node.x
            }

            is Op -> {
                val left = eval(nodes[node.left]!!)
                val right = eval(nodes[node.right]!!)
                node.res = when (node.op) {
                    '+' -> left + right
                    '-' -> left - right
                    '*' -> left * right
                    '/' -> left / right
                    else -> error("Bad op ${node.op}")
                }
                node.res
            }
        }
    }

    val res = eval(nodes["root"]!!)
    println("Part 1 ")
    println(res)

    println("Part 2")

    val wanted = "humn"

    fun contains(node: Node, name: String): Boolean {
        if (node.name == name) {
            node.isHumn = true
            node.containsHumn = true
            return true
        }
        when (node) {
            is Num -> {
                return false
            }

            is Op -> {
                val left = contains(nodes[node.left]!!, name)
                if (left) {
                    node.containsHumn = true
                    return true
                }
                val right = contains(nodes[node.right]!!, name)
                if (right) {
                    node.containsHumn = true
                    return true
                }
                return false
            }
        }
    }

    fun evaluate(node: Op, computed: Node, isLeft: Boolean): Long {
        val op = node.op
        val parentScore = node.res
        val otherChildScore = computed.res
        return when (op) {
            '+' -> {
                parentScore - otherChildScore
            }

            '*' -> {
                parentScore / otherChildScore
            }

            '-' -> {
                if (isLeft)
                    parentScore + otherChildScore
                else
                    otherChildScore - parentScore
            }

            '/' -> {
                if (isLeft)
                    parentScore * otherChildScore
                else
                    otherChildScore / parentScore

            }

            else -> error("bad op $op")
        }
    }

    fun dfs(node: Node) {
        if (node is Op) {
            val left = nodes[node.left]!!
            val right = nodes[node.right]!!
            val isLeft = left.containsHumn
            val (constant, computed) = if (isLeft) right to left else left to right
            val expected = evaluate(node, constant, isLeft)
            computed.res = expected
            dfs(computed)
        }
        if (node.name == wanted)
            println(node.res)
    }

    val root = nodes["root"]!! as Op
    val left = nodes[root.left]!!
    val right = nodes[root.right]!!

    var constant = left
    var computed = right
    if (contains(left, wanted)) {
        val tmp = constant
        constant = computed
        computed = tmp
    }

    computed.res = constant.res

    if (computed is Num) {
        println(computed.res)
    } else {
        val op = computed as Op
        dfs(op)
    }
}

