package com.example.busyday.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.busyday.R
import com.example.busyday.database.ActivityEntity
import com.example.busyday.database.CategoryEntity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class CategoryAdapter(private val categories: List<CategoryEntity>, private val activities: List<ActivityEntity>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val categoryDurationTextView: TextView =
            itemView.findViewById(R.id.categoryDurationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.categoryNameTextView.text = category.name
        holder.categoryDurationTextView.text =
            calculateCategoryDuration(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    private fun calculateCategoryDuration(category: CategoryEntity): String {
        var categoryDuration = 0L

        for (activity in activities) {
            if (activity.category == category.name) {
                val startTime = activity.startTime
                val endTime = activity.endTime
                val duration = calculateDuration(startTime, endTime)
                categoryDuration += duration
            }
        }

        return formatDuration(categoryDuration)
    }

    private fun calculateDuration(startTime: String, endTime: String): Long {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val startDate = format.parse(startTime)
            val endDate = format.parse(endTime)
            if (startDate != null && endDate != null) {
                val durationInMillis = endDate.time - startDate.time
                return durationInMillis
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0L
    }

    private fun formatDuration(durationInMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60
        return String.format("%02d:%02d", hours, minutes)
    }
}
