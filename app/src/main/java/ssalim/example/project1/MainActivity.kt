package ssalim.example.project1

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import jp.wasabeef.blurry.Blurry
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var cityName: EditText
    private lateinit var fetchYelpButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var yelpTextView: TextView
    private lateinit var mapView: MapView
    private lateinit var mapContainer: RelativeLayout

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
        mapContainer = findViewById(R.id.mapContainer)

        mapView = MapView(this)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        mapContainer.addView(mapView)



        fetchYelpButton.setOnClickListener {
            val city = cityName.text.toString()
            progressBar.visibility = View.VISIBLE
            viewModel.businesses(city)
        }

        viewModel.businessesLiveData.observe(this, Observer { businesses ->
            progressBar.visibility = View.GONE
            if (businesses != null) {
                displayBusinessMarkers(businesses)
                displayBusinessDetails(businesses)
                mapContainer.visibility = View.VISIBLE
            } else {
                yelpTextView.text = "Error, try again!"
            }
        })
    }

    private fun geocodeLocation(locationName: String): LatLng? {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(locationName, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            return LatLng(address.latitude, address.longitude)
        }
        return null
    }

    private fun displayBusinessMarkers(businesses: List<YelpData>?) {
        mapView.getMapAsync { googleMap ->
            googleMap.clear()

            businesses?.forEach { business ->
                val locationLatLng = LatLng(business.coordinates.latitude, business.coordinates.longitude)

                googleMap.addMarker(MarkerOptions().position(locationLatLng).title(business.name))
            }


            if (!businesses.isNullOrEmpty()) {
                val firstBusiness = businesses[0]
                val firstBusinessLocation = LatLng(firstBusiness.coordinates.latitude, firstBusiness.coordinates.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstBusinessLocation, 12f))
            }
        }
    }


    private fun displayBusinessDetails(businesses: List<YelpData>) {
        yelpTextView.text = businesses.joinToString("\n\n") { business ->
            "${business.name}\nRating: ${business.rating}\nPrice: ${business.price}\nLocation: ${business.location.address1}, ${business.location.city}, ${business.location.state}, ${business.location.country}\nPhone: ${business.phone}"
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
    }
}
