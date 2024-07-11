package ssalim.example.project1
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BusinessAdapter(
    private val businesses: List<YelpData>,
    private val onItemClicked: (YelpData) -> Unit
) : RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_business, parent, false)
        return BusinessViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        val business = businesses[position]
        holder.bind(business)
        holder.itemView.setOnClickListener {
            onItemClicked(business)
        }
    }

    override fun getItemCount(): Int = businesses.size

    class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.businessNameTextView)
        private val ratingTextView: TextView = itemView.findViewById(R.id.businessRatingTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.businessPriceTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.businessLocationTextView)

        fun bind(business: YelpData) {
            nameTextView.text = business.name
            ratingTextView.text = "Rating: ${business.rating}"
            priceTextView.text = "Price: ${business.price ?: "N/A"}"
            locationTextView.text = "${business.location.address1}, ${business.location.city}"
        }
    }
}