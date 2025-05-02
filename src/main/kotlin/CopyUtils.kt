package arsen.nersisyan

import java.util.IdentityHashMap
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

object CopyUtils {
    /**
     * Generic deep copy function.
     * @param obj - the object to deep copy.
     * @param visited - a map to handle cyclic references (original -> copied).
     */
    fun <T : Any> deepCopy(obj: T?, visited: MutableMap<Any, Any?> = IdentityHashMap()): T? {
        // 1. Null or primitives
        if (obj == null) return null
        if (obj is String || obj.javaClass.isPrimitive || obj is Number || obj is Boolean || obj is Enum<*>) {
            return obj
        }

        // 2. Cyclic references
        @Suppress("UNCHECKED_CAST")
        visited[obj]?.let { return it as T }

        // 3. Collections
        when (obj) {
            is List<*> -> {
                val copy = obj.map { deepCopy(it, visited) }
                visited[obj] = copy
                @Suppress("UNCHECKED_CAST")
                return copy as T
            }
            is Set<*> -> {
                val copy = obj.map { deepCopy(it, visited) }.toSet()
                visited[obj] = copy
                @Suppress("UNCHECKED_CAST")
                return copy as T
            }
            is Map<*, *> -> {
                val copy = obj.entries.associate { (k, v) ->
                    deepCopy(k, visited) to deepCopy(v, visited)
                }
                visited[obj] = copy
                @Suppress("UNCHECKED_CAST")
                return copy as T
            }
            is Array<*> -> {
                val copiedArray = java.lang.reflect.Array.newInstance(
                    obj.javaClass.componentType,
                    obj.size
                ) as Array<Any?>
                visited[obj] = copiedArray
                obj.forEachIndexed { index, element ->
                    copiedArray[index] = deepCopy(element, visited)
                }
                @Suppress("UNCHECKED_CAST")
                return copiedArray as T
            }
        }

        // 4. Default: custom class with primary constructor
        val kClass = obj::class
        val constructor = kClass.primaryConstructor
            ?: error("Class ${kClass.simpleName} must have a primary constructor to be deep copied")

        constructor.isAccessible = true

        val rawParams = constructor.parameters.associateWith { param ->
            kClass.memberProperties
                .firstOrNull { it.name == param.name }
                ?.apply { isAccessible = true }
                ?.getter?.call(obj)
        }

        val copy = constructor.callBy(rawParams)
        visited[obj] = copy

        kClass.memberProperties.forEach { prop ->
            prop.isAccessible = true
            val value = (prop as KProperty1<Any, Any?>).get(obj)
            val copiedValue = value?.let { deepCopy(it, visited) }
            if (prop is KMutableProperty1<*, *>) {
                (prop as KMutableProperty1<Any, Any?>).set(copy, copiedValue)
            }
        }

        return copy
    }
}
