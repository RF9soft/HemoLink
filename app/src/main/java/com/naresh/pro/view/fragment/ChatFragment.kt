import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.naresh.pro.R

class ChatFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initPlaces()
    }

    private fun initPlaces() {
        val apiKey = getString(R.string.google_maps_api_key)
        if (apiKey.isEmpty()) {
            // Handle missing API key
            return
        }

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }

        placesClient = Places.createClient(requireContext())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

            // Fetch the user's current location
            fetchUserLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchUserLocation() {
        val placeFields = listOf(Place.Field.LAT_LNG)

        val request = FindCurrentPlaceRequest.builder(placeFields).build()

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle the case where the user hasn't granted location permissions
            return
        }

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response: FindCurrentPlaceResponse? ->
                val place = response?.placeLikelihoods?.firstOrNull()?.place
                place?.latLng?.let { location ->
                    // Use the user's current location (location.latitude, location.longitude)
                    fetchNearbyPlaces(location)
                }
            }
            .addOnFailureListener { exception: Exception ->
                // Handle failure
                Log.e("PlacesAPI", "Error getting current place: ${exception.message}")
            }
    }

    private fun fetchNearbyPlaces(latLng: LatLng) {
        val placeType = Place.Type.HOSPITAL
        val radius = 5000.0

        val bounds = LatLngBounds.builder()
            .include(LatLng(latLng.latitude + radius, latLng.longitude + radius))
            .include(LatLng(latLng.latitude - radius, latLng.longitude - radius))
            .build()

        val request = FindCurrentPlaceRequest.builder(
            listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES)
        ).build()

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle the case where the user hasn't granted location permissions
            return
        }

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response: FindCurrentPlaceResponse? ->
                response?.placeLikelihoods?.forEach { likelihood ->
                    val place = likelihood.place
                    val placeLatLng = place.latLng

                    // Check if the place has the desired type
                    val placeTypes = place.types
                    if (placeLatLng != null && bounds.contains(placeLatLng) && placeTypes?.contains(placeType) == true) {
                        val placeName = place.name
                        val placeAddress = place.address

                        val markerOptions = MarkerOptions()
                            .position(placeLatLng)
                            .title(placeName)
                            .snippet(placeAddress)

                        map.addMarker(markerOptions)
                    }
                }
            }
            .addOnFailureListener { exception: Exception ->
                // Handle failure
                Log.e("PlacesAPI", "Error getting nearby places: ${exception.message}")
            }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
