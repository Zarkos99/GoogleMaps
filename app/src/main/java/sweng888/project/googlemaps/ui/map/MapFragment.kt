package sweng888.project.googlemaps.ui.map

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import sweng888.project.googlemaps.MainActivity
import sweng888.project.googlemaps.R
import sweng888.project.googlemaps.databinding.FragmentMapBinding
import java.util.Arrays
import kotlin.random.Random


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var m_google_map: GoogleMap
    private lateinit var m_places_client: PlacesClient

    // Define the search area as a 1000 meter diameter circle
    private val SEARCH_REGION_RADIUS = 1000.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), context?.getString(R.string.API_KEY)!!)
        }
        m_places_client = Places.createClient(requireContext())

        try {
            val map_fragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

            map_fragment.getMapAsync { google_map ->
                // Ensure all places are visible in the map
                google_map.setOnMapLoadedCallback {
                    m_google_map = google_map
                    binding.findRandomLocationButton.setOnClickListener {
                        changeMapFocusToRandomLocation()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MapFragment Error", "onCreateView: ", e)
            throw e
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeMapFocusToRandomLocation() {
        val random_lat = (Random.nextDouble() - 0.5) * 170 // -85 to +85 range
        val random_long = (Random.nextDouble() - 0.5) * 360  // 180 to +180 range
        makeMapsNearbySearchApiCall(LatLng(random_lat, random_long))
    }

    private fun makeMapsNearbySearchApiCall(lat_lng: LatLng) {

        val place_fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.WEBSITE_URI
        )

        val center = LatLng(lat_lng.latitude, lat_lng.longitude)
        val circle = CircularBounds.newInstance(center,  /* radius = */SEARCH_REGION_RADIUS)

        // Define a list of types to include.
        val included_types: List<String> =
            mutableListOf(
                "american_restaurant",
                "barbecue_restaurant",
                "brazilian_restaurant",
                "breakfast_restaurant",
                "brunch_restaurant",
                "cafe",
                "chinese_restaurant",
                "coffee_shop",
                "fast_food_restaurant",
                "french_restaurant",
                "greek_restaurant",
                "hamburger_restaurant",
                "ice_cream_shop",
                "indian_restaurant",
                "indonesian_restaurant",
                "italian_restaurant",
                "japanese_restaurant",
                "korean_restaurant",
                "lebanese_restaurant",
                "meal_delivery",
                "meal_takeaway",
                "mediterranean_restaurant",
                "mexican_restaurant",
                "middle_eastern_restaurant",
                "pizza_restaurant",
                "ramen_restaurant",
                "restaurant",
                "sandwich_shop",
                "seafood_restaurant",
                "spanish_restaurant",
                "steak_house",
                "sushi_restaurant",
                "thai_restaurant",
                "turkish_restaurant",
                "vegan_restaurant",
                "vegetarian_restaurant",
                "vietnamese_restaurant"
            )

        // Use the builder to create a SearchNearbyRequest object.
        val search_nearby_request =
            SearchNearbyRequest.builder(circle, place_fields)
                .setIncludedTypes(included_types)
                .setMaxResultCount(10)
                .build()

        var places: List<Place> = listOf()
        // Call placesClient.searchNearby() to perform the search.
        // Define a response handler to process the returned List of Place objects.
        m_places_client.searchNearby(search_nearby_request)
            .addOnSuccessListener { response ->
                places = response.places
                if (places.isNotEmpty()) {
                    changeMapFocus(lat_lng, places)
                } else {
                    changeMapFocusToRandomLocation()
                }
            }
            .addOnFailureListener {
                Log.e("NearbySearchFailure", "Nearby search failed: $it")
            }
    }

    private fun changeMapFocus(lat_lng: LatLng, search_results: List<Place>) {
        val main_activity: MainActivity? = activity as MainActivity?
        main_activity?.search_results = search_results

        m_google_map.clear()
        val camera_position = CameraPosition.builder()
            .target(lat_lng)
            .zoom(14f)
            .build()
        m_google_map.moveCamera(CameraUpdateFactory.newCameraPosition(camera_position))
        // Draw results circular region
        m_google_map.addCircle(
            CircleOptions().center(lat_lng).radius(SEARCH_REGION_RADIUS).fillColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimaryTranslucent
                )
            ).strokeColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        )
    }
}