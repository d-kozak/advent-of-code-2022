package day19

import java.io.FileInputStream
import kotlin.math.max

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day19/${fileName}"))


data class OreRobotPrice(val ore: Int)
data class ClayRobotPrice(val ore: Int)
data class ObsidianRobotPrice(val ore: Int, val clay: Int)
data class GeodeRobotPrice(val ore: Int, val obsidian: Int)
data class Config(
    val oreRobot: OreRobotPrice,
    val clayRobot: ClayRobotPrice,
    val obsidianRobot: ObsidianRobotPrice,
    val geodeRobot: GeodeRobotPrice
)

fun paseConfig(line: String): Config {
    val split = line.split(".")
    var r = """.*(\d+).*""".toRegex()
    var match = r.matchEntire(split[0])!!
    val oreRobot = OreRobotPrice(match.groups[1]!!.value.toInt())
    match = r.matchEntire(split[1])!!
    val clayRobot = ClayRobotPrice(match.groups[1]!!.value.toInt())

    r = """.*(\d+).+?(\d+).*""".toRegex()
    match = r.matchEntire(split[2])!!
    val obsidianRobot = ObsidianRobotPrice(
        match.groups[1]!!.value.toInt(),
        match.groups[2]!!.value.toInt()
    )

    match = r.matchEntire(split[3])!!
    val geodeRobot = GeodeRobotPrice(
        match.groups[1]!!.value.toInt(),
        match.groups[2]!!.value.toInt()
    )

    return Config(oreRobot, clayRobot, obsidianRobot, geodeRobot)
}

fun main() {
    setIn("test.txt")

    val configs = mutableListOf<Config>()
    var line = readlnOrNull()
    while (line != null) {
        val config = paseConfig(line)
        configs.add(config)
        line = readlnOrNull()
    }

    println("Part 1")
    var cumul = 0
    for ((i, config) in configs.withIndex()) {
        println(config)
        val bestScore = compute(config)
        println(bestScore)
        cumul += bestScore * (i + 1)
    }

    println("res: $cumul")

    println("Part 2")
    timeLimit = 32
    cumul = 1
    for (config in configs.take(3)) {
        println(config)
        val bestScore = compute(config)
        println(bestScore)
        cumul *= bestScore
    }

    println("res: $cumul")
}

fun compute(config: Config): Int {
    return dfs(1, 0, 0, 0, 0, 1, 0, 0, 0, config)
}

var timeLimit = 24

fun dfs(
    time: Int,
    ore: Int,
    clay: Int,
    obsidian: Int,
    geode: Int,
    oreRobots: Int,
    clayRobots: Int,
    obsidianRobots: Int,
    geodeRobots: Int,
    config: Config
): Int {
    if (time > timeLimit) return geode

    if (ore >= config.geodeRobot.ore && obsidian >= config.geodeRobot.obsidian) {
        return dfs(
            time + 1,
            ore + oreRobots - config.geodeRobot.ore,
            clay + clayRobots,
            obsidian + obsidianRobots - config.geodeRobot.obsidian,
            geode + geodeRobots,
            oreRobots,
            clayRobots,
            obsidianRobots,
            geodeRobots + 1,
            config
        )
    }

    if (ore >= config.obsidianRobot.ore && clay >= config.obsidianRobot.clay) {
        return dfs(
            time + 1,
            ore + oreRobots - config.obsidianRobot.ore,
            clay + clayRobots - config.obsidianRobot.clay,
            obsidian + obsidianRobots,
            geode + geodeRobots,
            oreRobots,
            clayRobots,
            obsidianRobots + 1,
            geodeRobots,
            config
        )
    }

    var score = dfs(
        time + 1,
        ore + oreRobots,
        clay + clayRobots,
        obsidian + obsidianRobots,
        geode + geodeRobots,
        oreRobots,
        clayRobots,
        obsidianRobots,
        geodeRobots,
        config
    )


    if (ore >= config.clayRobot.ore) {
        val x = dfs(
            time + 1,
            ore + oreRobots - config.clayRobot.ore,
            clay + clayRobots,
            obsidian + obsidianRobots,
            geode + geodeRobots,
            oreRobots,
            clayRobots + 1,
            obsidianRobots,
            geodeRobots,
            config
        )
        score = max(score, x)
    }

    if (ore >= config.oreRobot.ore) {
        val x = dfs(
            time + 1,
            ore + oreRobots - config.oreRobot.ore,
            clay + clayRobots,
            obsidian + obsidianRobots,
            geode + geodeRobots,
            oreRobots + 1,
            clayRobots,
            obsidianRobots,
            geodeRobots,
            config
        )
        score = max(score, x)
    }

    return score
}
