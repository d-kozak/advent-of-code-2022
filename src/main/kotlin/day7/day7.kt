package day7

import java.io.FileInputStream

enum class NodeType {
    DIR,
    FILE
}

data class FileTreeNode(
    var name: String,
    var type: NodeType,
    var size: Int = 0,
    var children: MutableList<FileTreeNode> = mutableListOf()
) {

    var parent: FileTreeNode? = null

    fun addChild(child: FileTreeNode) {
        children.add(child)
        child.parent = this
    }
}

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day7/${fileName}"))

fun main() {
    setIn("input.txt")
    val root = readInput()
    val smaller = traverse(root)
    println(smaller.sumOf { it.size })


    val totalDiskSize = 70_000_000
    val requiredFreeSize = 30_000_000
    val currentFreeSize = totalDiskSize - root.size
    val neededToRemove = requiredFreeSize - currentFreeSize
    val candidate = findDeleteCandidate(root, neededToRemove)
    println(candidate)
}

fun findDeleteCandidate(root: FileTreeNode, neededToRemove: Int): FileTreeNode {
    var candidate = root
    fun dfs(node: FileTreeNode) {
        if (node.type == NodeType.FILE)
            return
        if (node.size >= neededToRemove && node.size <= candidate.size) {
            candidate = node
        }
        for (child in node.children) {
            dfs(child)
        }
    }
    dfs(root)
    return candidate
}


fun traverse(root: FileTreeNode): List<FileTreeNode> {
    val limit = 100_000
    val smaller = mutableListOf<FileTreeNode>()

    fun dfs(node: FileTreeNode): Int {
        return when (node.type) {
            NodeType.FILE -> node.size
            NodeType.DIR -> {
                for (child in node.children) {
                    node.size += dfs(child)
                }
                if (node.size <= limit) smaller.add(node)
                node.size
            }
        }
    }

    dfs(root)
    return smaller
}

private fun readInput(): FileTreeNode {
    val root = FileTreeNode("/", type = NodeType.DIR)
    var curr = root
    var line = readlnOrNull()
    loop@ while (line != null) {
        println(line)
        when {
            line.startsWith("$") -> {
                val cmd = line.substring(2)
                when {
                    cmd == "ls" -> {
                        // nothing explicit to do, just process the output afterwards
                    }

                    cmd.startsWith("cd") -> {
                        when (val target = cmd.substring(3)) {
                            "/" -> curr = root
                            ".." -> curr = curr.parent!!
                            else -> {
                                for (child in curr.children) {
                                    if (child.name == target) {
                                        curr = child
                                        line = readlnOrNull()
                                        continue@loop
                                    }
                                }
                                error("Could not find dir $target in $curr")
                            }
                        }
                    }

                    else -> error("Unknown command $cmd")
                }
            }

            else -> {
                val (left, right) = line.split(' ')
                when (left) {
                    "dir" -> {
                        curr.addChild(FileTreeNode(right, type = NodeType.DIR))
                    }

                    else -> {
                        val name = right
                        val size = left.toInt()
                        curr.addChild(FileTreeNode(name, type = NodeType.FILE, size))
                    }
                }
            }
        }

        line = readlnOrNull()
    }

    return root
}