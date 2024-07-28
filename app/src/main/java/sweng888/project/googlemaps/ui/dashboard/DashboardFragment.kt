package sweng888.project.googlemaps.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import sweng888.project.googlemaps.MainActivity
import sweng888.project.googlemaps.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val main_activity: MainActivity? = activity as MainActivity?

        val recycler_view = binding.recyclerView
        val place_of_interest_adapter =
            PlaceOfInterestAdapter(requireContext(), main_activity?.search_results!!)
        recycler_view.setAdapter(place_of_interest_adapter)

        // Initialize FlexBox Layout Manager for recyclerview
        val layout_manager = FlexboxLayoutManager(context)
        layout_manager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        recycler_view.layoutManager = layout_manager

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}