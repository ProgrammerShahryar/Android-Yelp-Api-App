package ssalim.example.project1

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import retrofit2.Retrofit


class YelpRepository(private val retrofit: Retrofit) {
    private val gson = Gson()
    private val apiKey = "kTXDGeJKM77Gf1smt3UxWwEVIjkcY6ZzN3r3UNR0XLo1DMvJhnNFnzbw1jixLZcwnS9FJnjnAGMX-bjBJQ53p35ry5QfD-GxTo1afRuA2euoYbvntm9HkeOcOwSPZnYx"
    private val apiKey2 = "ffyY0mVF6f2dj-S356NVcrwRb4-NH_DjMgBsf6Ox-boz6mTzSlNjJj4V-4z4WLM9oeSjHo-08A9KJ6TDGxQ6YPNaNwEIsv7xCqYfpQs3CpEQPEGFI1PLap57pUqQZnYx"


    suspend fun sGetBusiness(location: String, businessesLiveData: MutableLiveData<List<YelpData>>) {
        val yelpService = retrofit.create(YelpService::class.java)
        try {
            val response = yelpService.yelpSearchResponse("Bearer $apiKey", "restaurants", location)
            businessesLiveData.postValue(response.businesses)
        } catch (e: Exception) {
            businessesLiveData.postValue(null)
        }
    }
}