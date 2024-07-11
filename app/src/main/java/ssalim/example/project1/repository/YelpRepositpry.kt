package ssalim.example.project1

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import retrofit2.Retrofit


class YelpRepository(private val retrofit: Retrofit) {
    private val gson = Gson()
    private val apiKey = "kTXDGeJKM77Gf1smt3UxWwEVIjkcY6ZzN3r3UNR0XLo1DMvJhnNFnzbw1jixLZcwnS9FJnjnAGMX-bjBJQ53p35ry5QfD-GxTo1afRuA2euoYbvntm9HkeOcOwSPZnYx"

    suspend fun getBuisness(location: String, businessesLiveData: MutableLiveData<List<YelpData>>) {
        val yelpService = retrofit.create(YelpService::class.java)
        try {
            val response = yelpService.yelpSearchResponse("Bearer $apiKey", "restaurants", location)
            businessesLiveData.postValue(response.businesses)
        } catch (e: Exception) {
            businessesLiveData.postValue(null)
        }
    }
}