package com.tehnokotiki.stroymatch.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehnokotiki.stroymatch.R
import com.tehnokotiki.stroymatch.model.RankedWorker
import com.tehnokotiki.stroymatch.model.RankedEmployer

class CardAdapter(
    private val workers: List<RankedWorker>? = null,
    private val employers: List<RankedEmployer>? = null
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatar)
        val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val occupationTextView: TextView = itemView.findViewById(R.id.tv_occupation)
        val scoreTextView: TextView = itemView.findViewById(R.id.tv_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        if (workers != null) {
            val worker = workers[position]
            holder.titleTextView.text = worker.worker.information.name
            holder.occupationTextView.text = worker.worker.search_criteria.work_experience

            val percentage = worker.match_info.matches.map { it.percentage }.average().toFloat()
            holder.scoreTextView.text = "${percentage.toInt()} %"
            holder.scoreTextView.setTextColor(getScoreColor(percentage))

            // Placeholder for avatar
            holder.avatarImageView.setImageResource(R.drawable.ic_avatar_placeholder)
        } else if (employers != null) {
            val employer = employers[position]
            holder.titleTextView.text = employer.employer.information.industry
            holder.occupationTextView.text = employer.employer.information.company_size

            val percentage = employer.match_info.matches.map { it.percentage }.average().toFloat()
            holder.scoreTextView.text = "${percentage.toInt()} %"
            holder.scoreTextView.setTextColor(getScoreColor(percentage))

            // Placeholder for avatar
            holder.avatarImageView.setImageResource(R.drawable.ic_avatar_placeholder)
        }
    }

    override fun getItemCount(): Int {
        return workers?.size ?: employers?.size ?: 0
    }

    private fun getScoreColor(percentage: Float): Int {
        return when (percentage) {
            in 0f..25f -> Color.RED
            in 25f..50f -> Color.parseColor("#DC4D01")
            in 50f..75f -> Color.parseColor("#ADFF2F") // Light green
            else -> Color.GREEN
        }
    }
}
