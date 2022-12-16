package day16

import java.io.FileInputStream
import kotlin.math.max
import kotlin.math.min

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day15/${fileName}"))


data class Node(val name: String, val index: Int, var flow: Int = 0) {
    var flowIndex = 0
    val nei: MutableList<Node> = mutableListOf()
}

data class Graph(val nodes: MutableMap<String, Node> = mutableMapOf()) {
    fun getOrCreateNode(name: String): Node = nodes.computeIfAbsent(name) { Node(name, nodes.size) }
}


fun parseGraph(): Graph {
    val graph = Graph()
    var line = readlnOrNull()
    while (line != null) {
        val node = graph.parseNode(line)
        line = readlnOrNull()
    }
    return graph
}

fun Graph.parseNode(line: String): Node {
    var i = 0
    val n = line.length
    while (line[i] != ' ') i++
    i++
    var end = i
    while (line[end] != ' ') end++
    val name = line.substring(i, end)
    i = end
    val node = getOrCreateNode(name)
    while (line[i] != '=') i++
    i++
    end = i
    while (line[end].isDigit()) end++
    node.flow = line.substring(i, end).toInt()
    i = end

    while (!line[i].isUpperCase()) i++

    while (i < n) {
        end = i
        while (end < n && line[end].isUpperCase()) {
            end++
        }
        val neiName = line.substring(i, end)
        val nei = getOrCreateNode(neiName)
        node.nei.add(nei)
//        nei.nei.add(node)
        while (end < n && (line[end] == ',' || line[end] == ' ')) end++
        i = end
    }

    return node
}


fun dumpGraph(graph: Graph) {
    for (node in graph.nodes.values) {
        print("${node.name} ${node.flow} nei:")
        println(node.nei.joinToString { it.name })
    }
}


val lim = 1e3.toInt()
fun main() {
    setIn("test.txt")
    val graph = parseGraph()
    dumpGraph(graph)

    val n = graph.nodes.size
    val dist = Array(n) { i -> IntArray(n) { j -> if (i == j) 0 else lim } }


    for (node in graph.nodes.values) {
        for (nei in node.nei) {
            dist[node.index][nei.index] = 1
            dist[nei.index][node.index] = 1
        }
    }

    for (i in 0 until n) {
        for (from in graph.nodes.values) {
            for (to in graph.nodes.values) {
                for (via in graph.nodes.values) {
                    dist[from.index][to.index] =
                        min(dist[from.index][to.index], dist[from.index][via.index] + dist[via.index][to.index])
                }
            }
        }
    }

    val indices = graph.nodes.values.sortedBy { it.index }.toTypedArray()

    println(" " + indices.map { it.name }.joinToString())
    for ((i, row) in dist.withIndex()) {
        print(indices[i].name + " ")
        println(row.joinToString())
    }

    val nonZeroNodes = mutableListOf<Node>()
    for (node in graph.nodes.values) {
        if (node.flow != 0) {
            node.flowIndex = nonZeroNodes.size
            nonZeroNodes.add(node)
        }
    }

    fun dfs(i: Int, valves: Int, time: Int): Int {
        var candidate = -1
        var candidateScore = -1
        for (j in 0 until nonZeroNodes.size) {
            val isSet = 1 shl j and valves != 0
            if (!isSet) {
                val moves = dist[i][nonZeroNodes[j].index] + 1
                val nextTime = time - moves
                if (nextTime >= 0) {
                    val score = nextTime * nonZeroNodes[j].flow
                    val nextSteps = dfs(nonZeroNodes[j].index, 1 shl j or valves, nextTime)
                    if (score + nextSteps > candidateScore) {
                        candidate = j
                        candidateScore = score + nextSteps
                    }
                }
            }
        }

        if (candidate == -1) {
            // no more closed non-zero valves
            return 0
        }

        return candidateScore
    }

    println("part 1")
    val startNode = graph.nodes["AA"]!!
    val res = dfs(startNode.index, 0, 30)
    println(res)


    // part 2


    fun dfs2(myPos: Int, elPos: Int, valves: Int, time: Int): Int {
        var bestScore = 0

        val canOpenMyValve = indices[myPos].flow > 0 && valves and 1 shl indices[myPos].flowIndex == 0
        val canOpenElephantValve = indices[elPos].flow > 0 && valves and 1 shl indices[elPos].flowIndex == 0
        if (canOpenMyValve) {
            val myOpenValveScore = (time - 1) * indices[myPos].flow
            if (canOpenElephantValve) {
                val score = myOpenValveScore + (time - 1) * indices[elPos].flow
                val nextSteps =
                    dfs(myPos, elPos, valves or 1 shl indices[myPos].flowIndex or 1 shl indices[elPos].flowIndex)
                bestScore = max(score, bestScore)
            }
            for (elNextPos in indices[elPos].nei) {

            }
        }

        for (myNextPos in indices[myPos].nei) {

            for (elNextPos in indices[elPos].nei) {

            }
        }

        return bestScore
    }


    println("part 2")
    val res2 = dfs2(startNode.index, startNode.index, 0, 30)
    println(res2)
}
