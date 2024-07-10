package ssalim.example.project1

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private val BASE_URL = "https://api.yelp.com/v3/"

    val retrofit by lazy { getRetrofitProvider() }

    private fun getRetrofitProvider(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }
}