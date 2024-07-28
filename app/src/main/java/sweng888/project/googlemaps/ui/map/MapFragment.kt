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
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import sweng888.project.googlemaps.MainActivity
import sweng888.project.googlemaps.R
import sweng888.project.googlemaps.databinding.FragmentMapBinding
import kotlin.random.Random


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        try {
            val map_fragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

            map_fragment.getMapAsync { googleMap ->
                // Ensure all places are visible in the map
                googleMap.setOnMapLoadedCallback {
                    binding.findRandomLocationButton.setOnClickListener {
                        changeMapFocusToRandomLocation(googleMap)
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

    private fun changeMapFocusToRandomLocation(googleMap: GoogleMap): JSONObject {

        var lat_lng = LatLng(0.0, 0.0)
        var no_results = true
        var nullable_search_results: JSONObject? = null

        while (no_results) {
            val random_lat = (Random.nextDouble() - 0.5) * 170 // -85 to +85 range
            val random_long = (Random.nextDouble() - 0.5) * 360  // 180 to +180 range
            lat_lng = LatLng(random_lat, random_long)

            nullable_search_results = makeMapsNearbySearchApiCall(lat_lng)
            no_results =
                nullable_search_results == null || nullable_search_results.getString("status") == "ZERO_RESULTS"
        }

        val main_activity: MainActivity? = activity as MainActivity?
        main_activity?.search_results = nullable_search_results!!

        googleMap.clear()
        val camera_position = CameraPosition.builder()
            .target(lat_lng)
            .zoom(14f)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera_position))
        // Draw results circular region
        googleMap.addCircle(
            CircleOptions().center(lat_lng).radius(1000.0).fillColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimaryTranslucent
                )
            ).strokeColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        )

        return nullable_search_results
    }

    private fun makeMapsNearbySearchApiCall(lat_lng: LatLng): JSONObject? {
        val request = Request.Builder().url(
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${lat_lng.latitude},${lat_lng.longitude}&radius=1000&type=all&key=${
                context?.getString(R.string.API_KEY)
            }"
        )
            .build()

        val response = OkHttpClient().newCall(request).execute().body?.string()
        return response?.let { JSONObject(it) } // This will make the json below as an object for you
    }
}