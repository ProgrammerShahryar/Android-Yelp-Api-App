package ssalim.example.project1


data class YelpData(
    val name: String,
    val rating: Double,
    val price: String?,
    val location: Location,
    val phone: String,
    val coordinates: YelpCoordinates

)



data class Location(
    val address1: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)
data class YelpCoordinates(
    val latitude: Double,
    val longitude: Double
)
