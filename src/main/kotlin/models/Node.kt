package arsen.nersisyan.models

data class Node(var value: Int, var next: Node? = null) {

    override fun toString(): String {
        return "Node(value=$value, next=${next?.value})"
    }

    override fun hashCode(): Int {
        return value.hashCode() // Only hash value to avoid cycle
    }

    override fun equals(other: Any?): Boolean {
        return other is Node && other.value == this.value
    }
}