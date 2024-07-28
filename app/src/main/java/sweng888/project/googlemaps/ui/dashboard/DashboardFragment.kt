package sweng888.project.googlemaps.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        //TODO: delete debug
        Log.d("DEBUG", main_activity?.search_results.toString())
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}