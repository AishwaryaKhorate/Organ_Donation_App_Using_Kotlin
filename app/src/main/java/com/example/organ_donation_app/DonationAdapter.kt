package com.example.organ_donation_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.organ_donation_app.R
import com.example.organ_donation_app.model.Donation

class DonationAdapter(private val donationList: List<Donation>) :
    RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorName: TextView = itemView.findViewById(R.id.textName)
        val organ: TextView = itemView.findViewById(R.id.textOrgan)
        val contact: TextView = itemView.findViewById(R.id.textContact)
        val date: TextView = itemView.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donationList[position]
        holder.donorName.text = donation.donorName
        holder.organ.text = "Organ: ${donation.organ}"
        holder.contact.text = "Contact: ${donation.contact}"
        holder.date.text = "Date: ${donation.date}"
    }

    override fun getItemCount(): Int {
        return donationList.size
    }
}
