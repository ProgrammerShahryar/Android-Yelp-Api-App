package ssalim.example.project1


data class YelpData(
    val name: String,
    val rating: Double,
    val price: String?,
    val location: YelpLocation,
    val phone: String
)

data class YelpLocation(
    val address1: String,
    val city: String,
    val state: String,
    val country: String
)