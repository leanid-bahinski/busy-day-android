package com.example.busyday.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    var startTime: String,
    var endTime: String,
    var category: String,
    var photoBytes: ByteArray?
) : Parcelable {
    companion object {
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
            return byteArray?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        }
    }

    fun getStartTimeAsDate(): Date {
        return timeFormat.parse(startTime) ?: Date()
    }

    fun getEndTimeAsDate(): Date {
        return timeFormat.parse(endTime) ?: Date()
    }

    fun setStartTime(time: Date) {
        startTime = timeFormat.format(time)
    }

    fun setEndTime(time: Date) {
        endTime = timeFormat.format(time)
    }
}
