import arsen.nersisyan.CopyUtils
import models.Address
import models.Man
import models.NoPrimaryConstructor
import models.Person
import models.Node
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame

class DeepCopyTest {

    @Test
    fun testManDeepCopy() {
        val original = Man("Arsen", 30, listOf("Kotlin in Action", "Effective Java"))
        val copied = CopyUtils.deepCopy(original)

        assertEquals(original, copied)
        assertNotSame(original, copied)
        assertNotSame(original.favoriteBooks, copied?.favoriteBooks)
        println("✅Test Man Deep Copy passed")
    }

    @Test
    fun testPrimitiveAndStringCopy() {
        val primitiveInt = 42
        val copiedInt = CopyUtils.deepCopy(primitiveInt)
        assertEquals(primitiveInt, copiedInt)

        val stringVal = "Hello"
        val copiedString = CopyUtils.deepCopy(stringVal)
        assertEquals(stringVal, copiedString)

        // Strings are immutable, should be the same reference
        assertSame(stringVal, copiedString)
        println("✅Test Primitive and String Copy passed")
    }

    @Test
    fun testListSetMapArrayCopy() {
        val list = listOf("Kotlin", "Java")
        val copiedList = CopyUtils.deepCopy(list)
        assertEquals(list, copiedList)
        assertNotSame(list, copiedList)

        val set = setOf(1, 2, 3)
        val copiedSet = CopyUtils.deepCopy(set)
        assertEquals(set, copiedSet)
        assertNotSame(set, copiedSet)

        val map = mapOf("key1" to 100, "key2" to 200)
        val copiedMap = CopyUtils.deepCopy(map)
        assertEquals(map, copiedMap)
        assertNotSame(map, copiedMap)

        val array = arrayOf("A", "B", "C")
        val copiedArray = CopyUtils.deepCopy(array)
        assertEquals(array.toList(), copiedArray?.toList())
        assertNotSame(array, copiedArray)
        println("✅Test List Set Map Array Copy passed")
    }

    @Test
    fun testCustomDataClassCopy() {
        val address = Address("Yerevan", "0014")
        val person = Person("Arsen", 33, address, listOf("Developer", "Engineer"))
        val copiedPerson = CopyUtils.deepCopy(person)

        assertEquals(person, copiedPerson)
        assertNotSame(person, copiedPerson)
        assertNotSame(person.address, copiedPerson?.address)
        println("✅Test Custom Data Class Copy passed")
    }

    @Test
    fun testCyclicNodeCopy() {
        val node1 = Node(1)
        val node2 = Node(2)
        node1.next = node2
        node2.next = node1 // cycle!

        val copiedNode1 = CopyUtils.deepCopy(node1)

        assertEquals(1, copiedNode1?.value)
        assertEquals(2, copiedNode1?.next?.value)
        assertTrue(copiedNode1?.next?.next === copiedNode1)
        println("✅Test Cyclic Node Copy passed")
    }

    @Test
    fun testDeepCopyFailsOnNoPrimaryConstructor() {
        val obj = NoPrimaryConstructor("no_primary_constructor")

        val exception = assertFailsWith<IllegalStateException> {
            CopyUtils.deepCopy(obj)
        }

        assert(exception.message!!.contains("must have a primary constructor"))
        println("✅Test Deep Copy Fails On No Primary Constructor passed")
    }
}
