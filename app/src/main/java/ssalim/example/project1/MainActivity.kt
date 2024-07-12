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
    private lateinit var sMapView: MapView
    private lateinit var sMapContainer: RelativeLayout
    private lateinit var sBusinessRecyclerView: RecyclerView
    private lateinit var sCityName: EditText
    private lateinit var sFetchYelpButton: Button


    private lateinit var sProgressBar: ProgressBar


    private val viewModel: YelpViewModel by viewModels { YelpViewModelFactory(YelpRepository(RetrofitProvider.retrofit)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sMapView = MapView(this)



        sProgressBar = findViewById(R.id.progressBar)
        sMapContainer = findViewById(R.id.mapContainer)

        sCityName = findViewById(R.id.cityEditText)

        sFetchYelpButton = findViewById(R.id.fetchYelpButton)

        sBusinessRecyclerView = findViewById(R.id.businessRecyclerView)


        findViewById<Button>(R.id.aboutButton).setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        sMapView.onCreate(savedInstanceState)
        sMapView.getMapAsync(this)






        sMapContainer.addView(sMapView)

        sBusinessRecyclerView.layoutManager = LinearLayoutManager(this)
        sFetchYelpButton.setOnClickListener { val city = sCityName.text.toString()
            sProgressBar.visibility = View.VISIBLE


            viewModel.sBusinesses(city)
        }

        sCityName.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            if (query.isNotEmpty()) {
                sEnableAutoSuggestions(query)
            }
        }

        viewModel.businessesLiveData.observe(this, Observer { businesses -> sProgressBar.visibility = View.GONE
            if (businesses != null) { showBusinessSpots(businesses)
                showBusinessInfo(businesses)


                sMapContainer.visibility = View.VISIBLE



                sBusinessRecyclerView.visibility = View.VISIBLE
            } else { Snackbar.make(sMapContainer, "No businesses found", Snackbar.LENGTH_LONG).show() } })
    }


    private fun sEnableAutoSuggestions(query: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(query, 5)

            val suggestions = addresses?.map { address ->
                address.getAddressLine(0)
            }

            if (suggestions != null) {
                viewModel.sGetAutoSuggestions(suggestions)
            }

        } catch (e: IOException) {



            e.printStackTrace()
            Snackbar.make(sMapContainer, "Error with suggestions", Snackbar.LENGTH_LONG).show()
        }
    }



    private fun showBusinessSpots(businesses: List<YelpData>?) {
        sMapView.getMapAsync { googleMap -> googleMap.clear()
            businesses?.forEach { business -> val locationLatLng = LatLng(business.coordinates.latitude, business.coordinates.longitude)
                googleMap.addMarker(MarkerOptions().position(locationLatLng).title(business.name))
            }


            if (!businesses.isNullOrEmpty()) { val firstBusiness = businesses[0]
                val firstBusinessLocation = LatLng(firstBusiness.coordinates.latitude, firstBusiness.coordinates.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstBusinessLocation, 12f)) } }
    }

    private fun showBusinessInfo(businesses: List<YelpData>) {
        val adapter = BusinessAdapter(businesses) { business -> sMapView.getMapAsync { googleMap -> val locationLatLng = LatLng(business.coordinates.latitude, business.coordinates.longitude)
                googleMap.clear()
                val markerOptions = MarkerOptions()
                    .position(locationLatLng)
                    .title(business.name)

                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                googleMap.addMarker(markerOptions)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f)) } }
        sBusinessRecyclerView.adapter = adapter }



    override fun onMapReady(googleMap: GoogleMap) { googleMap.uiSettings.isZoomControlsEnabled = true }



    override fun onResume() { super.onResume()
        sMapView.onResume() }

    override fun onPause() { super.onPause()
        sMapView.onPause() }

    override fun onDestroy() { super.onDestroy()
        sMapView.onDestroy() }

    override fun onLowMemory() { super.onLowMemory()
        sMapView.onLowMemory() }
}
