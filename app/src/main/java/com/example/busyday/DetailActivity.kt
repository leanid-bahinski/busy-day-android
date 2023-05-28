package com.example.busyday

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.busyday.database.ActivityEntity
import com.example.busyday.database.ActivityEntity.Companion.byteArrayToBitmap
import com.example.busyday.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var activity: ActivityEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activity = intent.getParcelableExtra<ActivityEntity>("activity")!!

        val imageView = findViewById<ImageView>(R.id.activity_image)
        val titleView = findViewById<TextView>(R.id.activity_title)
        val descriptionView = findViewById<TextView>(R.id.activity_description)
        val categoryView = findViewById<TextView>(R.id.activity_category)
        val startTimeView = findViewById<TextView>(R.id.activity_start_time)
        val endTimeView = findViewById<TextView>(R.id.activity_end_time)

        imageView.setImageBitmap(byteArrayToBitmap(activity.photoBytes) ?: defaultImage())
        titleView.text = activity.title
        descriptionView.text = activity.description
        categoryView.text = activity.category
        startTimeView.text = activity.startTime
        endTimeView.text = activity.endTime
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_submenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("activity", activity)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                showConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { dialog, _ ->
                val activityDao = AppDatabase.getDatabase(this).activityDao()
                GlobalScope.launch {
                    activityDao.deleteActivity(activity)
                }
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun defaultImage(): Bitmap {
        return BitmapFactory.decodeResource(this.resources, R.drawable.ic_activity)
    }
}
