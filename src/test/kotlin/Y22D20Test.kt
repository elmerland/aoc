import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Y22D20Test {

//    "1, 2, -3, 3, -2, 0, 4"

    @Test
    fun testPart1() {
        assertEquals("2(0), 1(1), -3(2), 3(3), -2(4), 0(5), 4(6)", Y22D20().part1())
    }

}