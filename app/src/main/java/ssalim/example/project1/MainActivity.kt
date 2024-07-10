package ssalim.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {

    private lateinit var cityName: EditText
    private lateinit var fetchYelpButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var yelpTextView: TextView

    private val viewModel: YelpViewModel by viewModels {
        YelpViewModelFactory(YelpRepository(RetrofitProvider.retrofit))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityName = findViewById(R.id.cityEditText)
        fetchYelpButton = findViewById(R.id.fetchYelpButton)
        progressBar = findViewById(R.id.progressBar)
        yelpTextView = findViewById(R.id.yelpTextView)

        fetchYelpButton.setOnClickListener {
            val city = cityName.text.toString()
            progressBar.visibility = View.VISIBLE
            viewModel.searchBusinesses(city)
        }

        viewModel.businessesLiveData.observe(this, Observer { businesses ->
            progressBar.visibility = View.GONE
            if (businesses != null) {
                yelpTextView.text = businesses.joinToString("\n\n") { business ->
                    "${business.name}\nRating: ${business.rating}\nPrice: ${business.price}\nLocation: ${business.location.address1}, ${business.location.city}, ${business.location.state}, ${business.location.country}\nPhone: ${business.phone}"
                }
            } else {
                yelpTextView.text = "Error, try again!"
            }
        })
    }
}