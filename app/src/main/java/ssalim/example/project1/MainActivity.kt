package ssalim.example.project1


import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import ssalim.example.project1.viewmodel.YelpViewModel
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var mapContainer: RelativeLayout
    private lateinit var businessRecyclerView: RecyclerView
    private lateinit var cityName: EditText
    private lateinit var fetchYelpButton: Button


    private lateinit var progressBar: ProgressBar


    private val viewModel: YelpViewModel by viewModels { YelpViewModelFactory(YelpRepository(RetrofitProvider.retrofit)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = MapView(this)
        progressBar = findViewById(R.id.progressBar)
        mapContainer = findViewById(R.id.mapContainer)

        cityName = findViewById(R.id.cityEditText)
        fetchYelpButton = findViewById(R.id.fetchYelpButton)

        businessRecyclerView = findViewById(R.id.businessRecyclerView)


        findViewById<Button>(R.id.aboutButton).setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        mapContainer.addView(mapView)

        businessRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchYelpButton.setOnClickListener { val city = cityName.text.toString()
            progressBar.visibility = View.VISIBLE


            viewModel.businesses(city)
        }

        cityName.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            if (query.isNotEmpty()) {
                fetchAutoSuggestions(query)
            }
        }

        viewModel.businessesLiveData.observe(this, Observer { businesses -> progressBar.visibility = View.GONE
            if (businesses != null) { displayBusinessMarkers(businesses)
                displayBusinessDetails(businesses)


                mapContainer.visibility = View.VISIBLE
                businessRecyclerView.visibility = View.VISIBLE
            } else { Snackbar.make(mapContainer, "No businesses found", Snackbar.LENGTH_LONG).show() } })
    }


    private fun fetchAutoSuggestions(query: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(query, 5)

            val suggestions = addresses?.map { address ->
                address.getAddressLine(0)
            }

            if (suggestions != null) {
                viewModel.updateAutoSuggestions(suggestions)
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Snackbar.make(mapContainer, "Error with suggestions", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun geocodeLocation(locationName: String): LatLng? { return try { val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null && addresses.isNotEmpty()) { val address = addresses[0]
                LatLng(address.latitude, address.longitude)
            } else { null }
        } catch (e: IOException) {e.printStackTrace()
            null } }



    private fun displayBusinessMarkers(businesses: List<YelpData>?) {
        mapView.getMapAsync { googleMap -> googleMap.clear()
            businesses?.forEach { business -> val locationLatLng = LatLng(business.coordinates.latitude, business.coordinates.longitude)
                googleMap.addMarker(MarkerOptions().position(locationLatLng).title(business.name))
            }


            if (!businesses.isNullOrEmpty()) { val firstBusiness = businesses[0]
                val firstBusinessLocation = LatLng(firstBusiness.coordinates.latitude, firstBusiness.coordinates.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstBusinessLocation, 12f)) } }
    }

    private fun displayBusinessDetails(businesses: List<YelpData>) {
        val adapter = BusinessAdapter(businesses) { business -> mapView.getMapAsync { googleMap -> val locationLatLng = LatLng(business.coordinates.latitude, business.coordinates.longitude)
                googleMap.clear()
                val markerOptions = MarkerOptions()
                    .position(locationLatLng)
                    .title(business.name)

                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                googleMap.addMarker(markerOptions)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f)) } }
        businessRecyclerView.adapter = adapter }



    override fun onMapReady(googleMap: GoogleMap) { googleMap.uiSettings.isZoomControlsEnabled = true }



    override fun onResume() { super.onResume()
        mapView.onResume() }

    override fun onPause() { super.onPause()
        mapView.onPause() }

    override fun onDestroy() { super.onDestroy()
        mapView.onDestroy() }

    override fun onLowMemory() { super.onLowMemory()
        mapView.onLowMemory() }
}
