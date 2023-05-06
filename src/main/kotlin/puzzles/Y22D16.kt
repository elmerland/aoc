package puzzles

import Puzzle

class Y22D16(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val re = Regex("^Valve (\\w{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.+)\$")
        val data = input().map { line ->
            val (valve, flowRate, valves) = re.find(line)!!.destructured
            Triple(valve, flowRate.toInt(), valves.split(", "))
        }

        val adj = mutableMapOf<String, List<String>>()
        data.forEach { (valve, _, connectingValves) -> adj[valve] = connectingValves}
        val usableValves = data.filter { (_, flowRate, _) -> flowRate > 0 }.map { it.first }
        val valves = data.map { it.first }.toSet()
        val valveFlowRates = data.map { it.first to it.second }.associate { it }

        val m = mutableMapOf<String, MutableMap<String, Int>>()
            .apply {
                valves.forEach { put(it, mutableMapOf()) }
                valves.forEach { get(it)!![it] = 0 }
                adj.forEach { (valve, connections) ->
                    connections.forEach { get(valve)!![it] = 1 }
                }
            }

        // Compute all distances
        floydWarshall(m)

        fun getFlow(valves: Set<String>) = valves.sumOf { valveFlowRates[it]!! }

        fun dfs(currValve: String, time: Int, total: Int, openValves: Set<String>): Int {
            // Score if we do nothing
            val currFlow = getFlow(openValves)
            val max = total + currFlow * (30 - time)

            return usableValves.mapNotNull { valve ->
                if (openValves.contains(valve)) return@mapNotNull null

                val timeDistance = m.getDistance(currValve, valve) + 1
                if (timeDistance + time >= 30) {
                    return@mapNotNull null
                }

                // New total flow if we move to valve and open it
                val newTotal = total +  timeDistance * currFlow
                dfs(valve, time + timeDistance, newTotal, openValves + valve)
            }.maxOrNull()?.let { if (it > max) it else max } ?: max
        }

        return dfs("AA", 0, 0, emptySet()).toString()
    }

    override fun part2(): String {
        val re = Regex("^Valve (\\w{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.+)\$")
        val data = input().map { line ->
            val (valve, flowRate, valves) = re.find(line)!!.destructured
            Triple(valve, flowRate.toInt(), valves.split(", "))
        }

        val adj = mutableMapOf<String, List<String>>()
        data.forEach { (valve, _, connectingValves) -> adj[valve] = connectingValves}
        val usableValves = data.filter { (_, flowRate, _) -> flowRate > 0 }.map { it.first }.toSet()
        val valves = data.map { it.first }.toSet()
        val valveFlowRates = data.map { it.first to it.second }.associate { it }

        val m = mutableMapOf<String, MutableMap<String, Int>>()
            .apply {
                valves.forEach { put(it, mutableMapOf()) }
                valves.forEach { get(it)!![it] = 0 }
                adj.forEach { (valve, connections) ->
                    connections.forEach { get(valve)!![it] = 1 }
                }
            }

        // Compute all distances
        floydWarshall(m)

        fun getFlow(valves: Set<String>) = valves.sumOf { valveFlowRates[it]!! }

        fun dfs(
            currValve: String,
            elephant: Boolean,
            time: Int,
            total: Int,
            openValves: Set<String>,
            usefulValves: Set<String>,
        ): Int {
            // Score if we do nothing
            val currFlow = getFlow(openValves)
            var currScore = total + currFlow * (26 - time)

            // If not acting as elephant, determine if adding an elephant would get a higher score
            if (!elephant) {
                // New current score is when we do nothing, and we let the elephant run around
                currScore += dfs("AA", true, 0, 0, emptySet(), usefulValves - openValves)
            }

            return maxOf(currScore, usefulValves.mapNotNull { valve ->
                if (openValves.contains(valve)) return@mapNotNull null

                val timeDistance = m.getDistance(currValve, valve) + 1
                if (timeDistance + time >= 26) {
                    return@mapNotNull null
                }

                // New total flow if we move to valve and open it
                val newTotal = total +  timeDistance * currFlow
                dfs(valve, elephant, time + timeDistance, newTotal, openValves + valve, usefulValves)
            }.maxOrNull() ?: Int.MIN_VALUE)
        }

        return dfs("AA", false, 0, 0, emptySet(), usableValves).toString()
    }

    private fun floydWarshall(m: MutableMap<String, MutableMap<String, Int>>) {
        val valves = m.keys

        valves.forEach { k ->
            valves.forEach { i ->
                valves.forEach { j ->
                    val currDist = m.getDistance(i, j)
                    val newDist = m.getDistance(i, k) + m.getDistance(k, j)
                    if (newDist < currDist) {
                        m.setDistance(i, j, newDist)
                    }
                }
            }
        }
    }

    private fun Map<String, Map<String, Int>>.getDistance(i: String, j: String) = get(i)!![j] ?: 1_000_000
    private fun Map<String, MutableMap<String, Int>>.setDistance(i: String, j: String, value: Int) {
        get(i)!![j] = value
    }

}

/**
 * The below is my first attempt at this problem using DP. It worked for part 1 and 2 with sample test. But part 2
 * runs out of heap space
 */
//        override fun part1(): String {
//        val re = Regex("^Valve (\\w{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.+)\$")
//        val flows = input.map { line ->
//            val match = re.find(line)!!
//            val (room, flowRate, connectingValves) = match.destructured
//            Triple(room, flowRate.toInt(), connectingValves.split(", "))
//        }
//
//        val nodes = mutableMapOf<String, Node<String, Int>>()
//        flows.forEach { (room, flowRate, connectingValves) ->
//            val n = nodes.getOrPut(room) { Node(room) }.also { it.value = flowRate }
//            val children = connectingValves.map { child ->
//                nodes.getOrPut(child) { Node(child) }.also { it.parents.add(n) }
//            }
//            n.children.addAll(children)
//        }
//
//        val valvesWithPressure = nodes.values.filter { it.value!! > 0 }.map { it.name }.toSet().toList()
//
//        data class Input(
//            val time: Int,
//            val room: String,
//            val valvesRemaining: List<String>,
//            val flow: Int,
//        )
//
//        val cache = mutableMapOf<Input, Int>()
//
//        fun dp(input: Input): Int {
//            val (time, room, valvesRemaining, flow) = input
//            if (time == 30) {
//                return cache.getOrPut(input) { flow }
//            }
//            if (valvesRemaining.isEmpty()) {
//                return cache.getOrPut(input) { flow * (30 - time + 1) }
//            }
//
//            val n = nodes[room]!!
//
//            // open curr valve
//            val openValveScore = if (valvesRemaining.contains(room)) {
//                val i = input.copy(time = time + 1, valvesRemaining = valvesRemaining - room, flow = flow + n.value!!)
//                flow + cache.getOrPut(i) { dp(i) }
//            } else null
//
//            // move to other rooms
//            val moveToAnotherRoom = n.children.maxOf {
//                val i = input.copy(time = time + 1, room = it.name)
//                flow + cache.getOrPut(i) { dp(i) }
//            }
//
//            return listOfNotNull(openValveScore, moveToAnotherRoom).max()
//        }
//
//
//
//        return dp(Input(1, "AA", valvesWithPressure, 0)).toString()
////        return ""
//    }
//
//    override fun part2(): String {
//        val re = Regex("^Valve (\\w{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.+)\$")
//        val flows = input.map { line ->
//            val match = re.find(line)!!
//            val (room, flowRate, connectingValves) = match.destructured
//            Triple(room, flowRate.toInt(), connectingValves.split(", "))
//        }
//
//        val nodes = mutableMapOf<String, Node<String, Int>>()
//        flows.forEach { (room, flowRate, connectingValves) ->
//            val n = nodes.getOrPut(room) { Node(room) }.also { it.value = flowRate }
//            val children = connectingValves.map { child ->
//                nodes.getOrPut(child) { Node(child) }.also { it.parents.add(n) }
//            }
//            n.children.addAll(children)
//        }
//
//        val valvesWithPressure = nodes.values.filter { it.value!! > 0 }.map { it.name }.toSet()
//
//        val reachableMap = nodes.keys.map { it to dijkstra(it, nodes).keys }.associate { it }
//
//        println(dijkstra("AA", nodes))
//
//        data class Input(
//            val time: Int,
//            val mroom: String,
//            val eroom: String,
//            val valvesRemaining: Set<String>,
//            val flow: Int,
////            val emove: Boolean,
//        )
//
//        val cache = mutableMapOf<Input, Int>()
//
//        fun dp(input: Input): Int {
//            val (time, mroom, eroom, valvesRemaining, flow) = input
//            if (time == 26) {
//                return cache.getOrPut(input) { flow }
//            }
//            if (valvesRemaining.isEmpty()) {
//                return cache.getOrPut(input) { flow * (26 - time + 1) }
//            }
//
//            val mpressure = nodes[mroom]!!.value!!
//            val epressure = nodes[eroom]!!.value!!
//            val mchildren = nodes[mroom]!!.children
//            val echildren = nodes[eroom]!!.children
//
//            val newInput = input.copy(time = time + 1)
//
//            return setOf(
//                "open" to "open",
//                "open" to "move",
//                "move" to "open",
//                "move" to "move"
//            ).mapNotNull { (maction, eaction) ->
//                val blockedFromOpen = (maction == "open" && !valvesRemaining.contains(mroom))
//                        || (eaction == "open" && !valvesRemaining.contains(eroom))
//
//                if (blockedFromOpen) {
//                    return@mapNotNull null
//                }
//
//                val noValidMoves =
//                    (maction == "move" && valvesRemaining.union(reachableMap[mroom]!!).isEmpty()) ||
//                            (eaction == "move" && valvesRemaining.union(reachableMap[eroom]!!).isEmpty())
//
//                if (noValidMoves) {
//                    return@mapNotNull null
//                }
//
//                when (maction to eaction) {
//                    "open" to "open" -> {
//                        if (mroom == eroom) {
//                            null
//                        } else {
//                            val i = newInput.copy(
//                                valvesRemaining = valvesRemaining - mroom - eroom,
//                                flow = flow + mpressure + epressure
//                            )
//                            flow + cache.getOrPut(i) { dp(i) }
//                        }
//                    }
//                    "open" to "move" -> {
//                        echildren.maxOf { echild ->
//                            val i = newInput.copy(
//                                valvesRemaining = valvesRemaining - mroom,
//                                flow = flow + mpressure,
//                                eroom = echild.name,
//                            )
//                            flow + cache.getOrPut(i) { dp(i) }
//                        }
//                    }
//                    "move" to "open" -> {
//                        mchildren.maxOf { mchild ->
//                            val i = newInput.copy(
//                                valvesRemaining = valvesRemaining - eroom,
//                                flow = flow + epressure,
//                                mroom = mchild.name,
//                            )
//                            flow + cache.getOrPut(i) { dp(i) }
//                        }
//                    }
//                    "move" to "move" -> {
//                        mchildren.maxOf { mchild ->
//                            echildren.maxOf { echild ->
//                                val i = newInput.copy(
//                                    mroom = mchild.name,
//                                    eroom = echild.name
//                                )
//                                flow + cache.getOrPut(i) { dp(i) }
//                            }
//                        }
//                    }
//                    else -> { throw IllegalStateException() }
//                }
//            }.max()
//        }
//        return dp(Input(1, "AA", "AA", valvesWithPressure, 0)).toString()
////        return ""
//    }
//
//    private fun dijkstra(root: String, adj: Map<String, Node<String, Int>>): Map<String, Int> {
//        val queue = ArrayDeque<Pair<String, Int>>()
//        queue.add(root to 0)
//
//        val distanceChart = mutableMapOf<String, Int>()
//
//        while (queue.isNotEmpty()) {
//            val minNode = queue.minBy { it.second }
//            queue.remove(minNode)
//            val node = minNode.first
//            val distance = minNode.second
//
//            if (distance > (distanceChart[node] ?: Int.MAX_VALUE)) {
//                continue
//            }
//
//            adj[node]?.children?.forEach { neighbor ->
//                val newDistance = distance + 1
//                if ((distanceChart[neighbor.name] ?: Int.MAX_VALUE) > newDistance) {
//                    distanceChart[neighbor.name] = newDistance
//                    queue.add(neighbor.name to newDistance)
//                }
//            }
//        }
//        return distanceChart
//    }
