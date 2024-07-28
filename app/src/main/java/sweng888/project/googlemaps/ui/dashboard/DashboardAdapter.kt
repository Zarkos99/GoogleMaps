package sweng888.project.googlemaps.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place
import sweng888.project.googlemaps.R


class PlaceOfInterestAdapter(private val context: Context, private val place_list: List<Place>) :
    RecyclerView.Adapter<PlaceOfInterestAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dashboard_item, parent, false)
        val card_view = view.findViewById<View>(R.id.location_card_view) as CardView
        card_view.useCompatPadding = true // Optional: adds padding for pre-lollipop devices
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, do_not_use: Int) {
        val place: Place = place_list[holder.getAdapterPosition()]
        holder.location_name.text = place.name

        // Provides logic to track all selected products as the user selects them
        holder.setItemClickListener(object : PlaceViewHolder.ItemClickListener {
            override fun onItemClick(v: View, pos: Int) {
                val clicked_place: Place = place_list[pos]
                // Notify user if the place they selected does not have a website, otherwise open the website
                if (clicked_place.websiteUri == null) {
                    val place_name =
                        if (clicked_place.name == null) clicked_place.name else "Selected place"
                    Toast.makeText(
                        context,
                        "$place_name does not have an associated website",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                val browser_intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(clicked_place.websiteUri?.toString()))
                startActivity(context, browser_intent, null)
            }
        })
    }

    override fun getItemCount(): Int {
        return place_list.size
    }

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val location_name: TextView = itemView.findViewById(R.id.location_name)
        lateinit var item_click_listener: ItemClickListener

        init {
            // Make the checkbox selectable
            itemView.setOnClickListener(this)
        }

        fun setItemClickListener(ic: ItemClickListener) {
            this.item_click_listener = ic
        }

        /**
         * Uses the View.OnClickListener inheritance to allow each list item to have clickable functionality
         */
        override fun onClick(v: View) {
            this.item_click_listener.onItemClick(v, layoutPosition)
        }

        interface ItemClickListener {
            fun onItemClick(v: View, pos: Int)
        }
    }
}