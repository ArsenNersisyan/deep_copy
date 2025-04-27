package arsen.nersisyan.models

data class Person(
    var name: String,
    var age: Int,
    var address: Address,
    var tags: List<String>
)

data class Address(
    var city: String,
    var zip: String
)