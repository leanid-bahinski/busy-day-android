package com.example.busyday.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.busyday.R
import com.example.busyday.database.ActivityEntity
import com.example.busyday.database.ActivityEntity.Companion.byteArrayToBitmap

class ActivityAdapter(context: Context, val resource: Int, val items: List<ActivityEntity>) :
    ArrayAdapter<ActivityEntity>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val activity = items[position]

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        val categoryTextView = view.findViewById<TextView>(R.id.categoryTextView)
        val timeTextView = view.findViewById<TextView>(R.id.timeTextView)

        imageView.setImageBitmap(byteArrayToBitmap(activity.photoBytes) ?: defaultImage())
        titleTextView.text = activity.title
        categoryTextView.text = activity.category
        timeTextView.text = activity.startTime + '-' + activity.endTime

        return view
    }

    private fun defaultImage(): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.ic_activity)
    }
}
