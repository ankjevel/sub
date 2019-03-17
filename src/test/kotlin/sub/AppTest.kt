package sub

import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
    @Test fun testShouldBeFixed() {
        assertNotNull("this text is fine", "this should not break")
    }
}
