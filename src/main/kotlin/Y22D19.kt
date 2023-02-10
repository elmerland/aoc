class Y22D19 : Puzzle(22, 19) {
    override fun part1(): String {
        val re = Regex("^Blueprint (\\d+): .* (\\d+) .* (\\d+) .* (\\d+) .* (\\d+) .* (\\d+) .* (\\d+) .*\$")
        val (bluePrint, oreRobot, clayRobot, obRobotOre, obRobotClay, geoRobotOre, geoRobotOb) = re.find(input[0])!!.destructured
        val x = maxGeodesForBlueprint(
            listOf(
                oreRobot,
                clayRobot,
                obRobotOre,
                obRobotClay,
                geoRobotOre,
                geoRobotOb
            ).map { num -> num.toInt() })
        println("RESULT $x")
//        val blueprints = input[0].mapIndexed { index, s ->
//            val (bluePrint, oreRobot, clayRobot, obRobotOre, obRobotClay, geoRobotOre, geoRobotOb) = re.find(s)!!.destructured
////            if (index > 0) { throw IllegalStateException() }
//            bluePrint.toInt() to maxGeodesForBlueprint(
//                listOf(
//                    oreRobot,
//                    clayRobot,
//                    obRobotOre,
//                    obRobotClay,
//                    geoRobotOre,
//                    geoRobotOb
//                ).map { num -> num.toInt() })
//        }

//        println(blueprints.joinToString("\n"))
        TODO("Not yet implemented")
    }

    override fun part2(): String {
        TODO("Not yet implemented")
    }

    private fun maxGeodesForBlueprint(blueprint: List<Int>): Int {

        println("blueprint config $blueprint")
        val oreRobotOreCost = blueprint[0]
        val clayRobotOreCost = blueprint[1]
        val obsidianRobotOreCost = blueprint[2]
        val obsidianRobotClayCost = blueprint[3]
        val geodeRobotOreCost = blueprint[4]
        val geodeRobotObsidianCost = blueprint[5]


        data class Store(
            val time: Int = 1,
            val ore: Int = 0,
            val clay: Int = 0,
            val obsidian: Int = 0,
            val geodes: Int = 0,
            val oreRobots: Int = 1,
            val clayRobots: Int = 0,
            val obsidianRobots: Int = 0,
            val geoRobots: Int = 0,
        ) {
            fun canAfford(robot: Robot): Boolean {
                return when (robot) {
                    Robot.ORE -> ore >= oreRobotOreCost
                    Robot.CLAY -> ore >= clayRobotOreCost
                    Robot.OBSIDIAN -> ore >= obsidianRobotOreCost && clay >= obsidianRobotClayCost
                    Robot.GEODE -> ore >= geodeRobotOreCost && obsidian >= geodeRobotObsidianCost
                }
            }

            fun canAffordAny(): Boolean {
                return Robot.values().any { canAfford(it) }
            }

            fun buyRobot(robot: Robot): Store {
                return when (robot) {
                    Robot.ORE -> this.copy(ore = ore - oreRobotOreCost, oreRobots = oreRobots + 1)
                    Robot.CLAY -> this.copy(ore = ore - clayRobotOreCost, clayRobots = clayRobots + 1)
                    Robot.OBSIDIAN -> this.copy(
                        ore = ore - obsidianRobotOreCost,
                        clay = clay - obsidianRobotClayCost,
                        obsidianRobots = obsidianRobots + 1
                    )

                    Robot.GEODE -> this.copy(
                        ore = ore - geodeRobotOreCost,
                        obsidian = obsidian - geodeRobotObsidianCost,
                        geoRobots = geoRobots + 1
                    )
                }
            }

            fun buyAsManyAsCanAfford(robot: Robot): Store {
                var tempStore = this
                while (tempStore.canAfford(robot)) {
                    tempStore = tempStore.buyRobot(robot)
                }
                return this
            }

            fun mine(): Store {
                return this.copy(
                    time = time + 1,
                    ore = ore + oreRobots,
                    clay = clay + clayRobots,
                    obsidian = obsidian + obsidianRobots,
                    geodes = geodes + geoRobots
                )
            }

            fun mineWithStartingRobots(startingStore: Store): Store {
                return this.copy(
                    time = time + 1,
                    ore = ore + startingStore.oreRobots,
                    clay = clay + startingStore.clayRobots,
                    obsidian = obsidian + startingStore.obsidianRobots,
                    geodes = geodes + startingStore.geoRobots
                )
            }
        }

        val cache = mutableMapOf<Store, Int>()

        fun generatePurchaseOptions(store: Store): List<Store> {
            return Robot.values().mapNotNull { if (store.canAfford(it)) store.buyRobot(it) else null }
        }

        fun dp(store: Store): Int {
//            println("DP ${store.time} - DETAILS $store")
            if (store.time > 24) {
                return cache.getOrPut(store) { store.geodes }
            }

            val buyNothing = store.mine()

            val purchaseOptions = generatePurchaseOptions(store).map { it.mineWithStartingRobots(store) }

            val alternatives = purchaseOptions + buyNothing
//            println("alternatives\n" + alternatives.joinToString("\n") {"\t" + it.toString()})

            return alternatives.maxOf { cache.getOrPut(it) { dp(it) } }
        }


//        var store = Store()
//
//        repeat(24) {
//            println("round $it")
//            store = store
//                .buyAsManyAsCanAfford(Robot.GEODE)
//                .buyAsManyAsCanAfford(Robot.OBSIDIAN)
//                .buyAsManyAsCanAfford(Robot.CLAY)
//                .buyAsManyAsCanAfford(Robot.ORE)
//                .mineWithStartingRobots(store)
//        }

//        return store.geodes
//        return dp(Store(23, 5, 37, 6, 7, 1, 4, 2, 2))
        return dp(Store())
    }

    enum class Robot {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE;
    }
}