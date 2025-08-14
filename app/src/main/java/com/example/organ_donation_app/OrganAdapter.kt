package com.example.organ_donation_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrganAdapter(
    private val organList: List<String>,
    private val onOrganSelected: (String) -> Unit
) : RecyclerView.Adapter<OrganAdapter.OrganViewHolder>() {

    class OrganViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val organImage: ImageView = itemView.findViewById(R.id.imageOrgan)
        val organName: TextView = itemView.findViewById(R.id.textOrganName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_organ_card, parent, false)
        return OrganViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrganViewHolder, position: Int) {
        val organ = organList[position]
        holder.organName.text = organ

        // Set organ image based on organ name
        val imageRes = when (organ.lowercase()) {
            "kidney" -> R.drawable.kidney
            "liver" -> R.drawable.liver
            "heart" -> R.drawable.heart
            "lungs" -> R.drawable.lungs
            "pancreas" -> R.drawable.pancreas
            "intestines" -> R.drawable.intestine
            "eye" -> R.drawable.eye
            "skin" -> R.drawable.skin
            "bone marrow" -> R.drawable.bone_marrow
            else -> R.drawable.ic_organ
        }
        holder.organImage.setImageResource(imageRes)

        // Save only the clicked organ
        holder.itemView.setOnClickListener {
            onOrganSelected(organ) // Pass back the clicked organ name
        }
    }

    override fun getItemCount(): Int = organList.size
}
