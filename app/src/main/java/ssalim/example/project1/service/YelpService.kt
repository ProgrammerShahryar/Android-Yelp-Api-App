package ssalim.example.project1

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpService {
    @GET("businesses/search")
    suspend fun searchBusinesses(
        @Header("Authorization") authHeader: String,
        @Query("term") term: String,
        @Query("location") location: String
    ): YelpSearchResponse
}

data class YelpSearchResponse(
    val businesses: List<YelpData>
)
