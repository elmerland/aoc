package puzzles

import Puzzle

class Y22D21(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {

    private val monkeyDescriptionRegex = Regex("^(\\w+): (.+)")
    val mathOperationRegex = Regex("(\\w+) (.) (\\w+)")

    override fun part1(): String {
        val monkeys = parseMonkeys().associateBy { it.name }

        val root = MonkeyNode(monkeys["root"]!!, null, null)

        val queue = ArrayDeque<MonkeyNode>()
        queue.addLast(root)

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            when (val action = curr.m.action) {
                is MathOperation -> {
                    curr.l = MonkeyNode(monkeys[action.leftOperand]!!, null, null)
                    curr.r = MonkeyNode(monkeys[action.rightOperand]!!, null, null)
                    queue.addLast(curr.l!!)
                    queue.addLast(curr.r!!)
                }
                is YellNumber -> {}
            }

        }

        return root.evaluate().toString()
    }

    override fun part2(): String {
        val monkeys = parseMonkeys().associateBy { it.name }

        val equality = monkeys["root"]!!
        check(equality.action is MathOperation)
        val leftMonkey = equality.action.leftOperand
        val rightMonkey = equality.action.rightOperand
        val root1 = MonkeyNode(monkeys[leftMonkey]!!, null, null)
        val root2 = MonkeyNode(monkeys[rightMonkey]!!, null, null)

        val queue = ArrayDeque<MonkeyNode>()
        queue.addLast(root1)
        queue.addLast(root2)

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            when (val action = curr.m.action) {
                is MathOperation -> {
                    curr.l = MonkeyNode(monkeys[action.leftOperand]!!, null, null)
                    curr.r = MonkeyNode(monkeys[action.rightOperand]!!, null, null)
                    queue.addLast(curr.l!!)
                    queue.addLast(curr.r!!)
                }
                is YellNumber -> {}
            }

        }

        val result = if (root1.hasHuman()) {
            println("root1 has a human")
            root1.evaluateHuman(root2.evaluate())
        } else {
            println("root2 has a human")
            root2.evaluateHuman(root1.evaluate())
        }

        return result.toString()
    }

    private fun parseMonkeys(): List<Monkey> {
        return input().map { line ->
            val (monkeyName, action) = monkeyDescriptionRegex.find(line)!!.destructured
            Monkey(
                monkeyName,
                action.toLongOrNull()?.let { YellNumber(it) } ?: makeMathOperation(action)
            )
        }
    }

    data class Monkey(val name: String, val action: MonkeyAction)

    class MonkeyNode(val m: Monkey, var l: MonkeyNode?, var r: MonkeyNode?) {
        fun evaluate(): Long {
            return when (m.action) {
                is YellNumber -> m.action.number
                is MathOperation -> {
                    val operand = m.action.operation
                    l!!.evaluate().operand(r!!.evaluate())
                }
            }
        }

        fun evaluateHuman(shouldEqual: Long): Long {
            return when (m.action) {
                is YellNumber -> {
                    if (isHuman()) {
                        shouldEqual
                    } else {
                        m.action.number
                    }
                }
                is MathOperation -> {
                    if (!r!!.hasHuman()) {
                        val rightVal = r!!.evaluate()
                        val rightOperand = m.action.reverseRightOperand
                        l!!.evaluateHuman(shouldEqual.rightOperand(rightVal))
                    } else {
                        val leftVal = l!!.evaluate()
                        val leftOperand = m.action.reverseLeftOperand
                        r!!.evaluateHuman(shouldEqual.leftOperand(leftVal))
                    }
                }
            }
        }

        fun hasHuman(): Boolean {
            return when (m.action) {
                is YellNumber -> isHuman()
                is MathOperation -> l!!.hasHuman() || r!!.hasHuman()
            }
        }

        private fun isHuman(): Boolean = m.name == HUMAN_LABEL
    }

    private fun makeMathOperation(raw: String): MathOperation {
        val (left, operator, right) = mathOperationRegex.find(raw)!!.destructured
        val t: Triple<Long.(Long) -> Long, Long.(Long) -> Long, Long.(Long) -> Long> = when (operator) {
            "+" -> plus
            "-" -> minus
            "*" -> times
            "/" -> div
            else -> throw UnsupportedOperationException()
        }
        return MathOperation(left, right, t.first, t.second, t.third)
    }

    sealed class MonkeyAction
    data class MathOperation(
        val leftOperand: String,
        val rightOperand: String,
        val operation: Long.(Long) -> Long,
        val reverseLeftOperand: Long.(Long) -> Long,
        val reverseRightOperand: Long.(Long) -> Long
    ) :
        MonkeyAction()
    data class YellNumber(val number: Long) : MonkeyAction()

    val plus = Triple<Long.(Long) -> Long, Long.(Long) -> Long, Long.(Long) -> Long>(
        Long::plus,
        fun Long.(a: Long) = this - a ,
        fun Long.(b: Long) = this - b,
    )

    val minus = Triple<Long.(Long) -> Long, Long.(Long) -> Long, Long.(Long) -> Long>(
        Long::minus,
        fun Long.(a: Long) = a - this,
        fun Long.(b: Long) = this + b,
    )

    val times = Triple<Long.(Long) -> Long, Long.(Long) -> Long, Long.(Long) -> Long>(
        Long::times,
        fun Long.(a: Long) = this / a,
        fun Long.(b: Long) = this / b,
    )

    val div = Triple<Long.(Long) -> Long, Long.(Long) -> Long, Long.(Long) -> Long>(
        Long::div,
        fun Long.(a: Long) = a / this,
        fun Long.(b: Long) = this * b,
    )

    companion object {
        const val HUMAN_LABEL = "humn"
    }
}