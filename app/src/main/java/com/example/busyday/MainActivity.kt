package com.example.busyday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import com.example.busyday.adapters.ActivityAdapter
import com.example.busyday.database.ActivityDao
import com.example.busyday.database.ActivityEntity
import com.example.busyday.database.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var activityAdapter: ActivityAdapter
    private val activities = mutableListOf<ActivityEntity>()
    private lateinit var activityDao: ActivityDao
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)
        activityAdapter = ActivityAdapter(this, R.layout.activity_list_item, activities)
        listView.adapter = activityAdapter

        registerForContextMenu(listView)

        val appDatabase = AppDatabase.getDatabase(this)
        activityDao = appDatabase.activityDao()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("activity", activities[position])
            startActivity(intent)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateActivityList() {
        GlobalScope.launch {
            val activityList = activityDao.getAllActivities()
            activities.clear()
            activities.addAll(activityList.filter { activityEntity ->
                activityEntity.title.contains(searchQuery, ignoreCase = true)
            })
            withContext(Dispatchers.Main) {
                activityAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateActivityList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText.orEmpty()
                updateActivityList()
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                sortActivities()
                true
            }
            R.id.action_statistic -> {
                val intent = Intent(this, CategoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sortActivities() {
        activities.sortBy { it.title }
        activityAdapter.notifyDataSetChanged()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position
        val activity = activities[position]

        return when (item.itemId) {
            R.id.action_detail -> {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("activity", activities[position])
                startActivity(intent)
                true
            }
            R.id.action_edit -> {
                openEditActivity(activity)
                true
            }
            R.id.action_delete -> {
                showConfirmationDialog(activity)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun openEditActivity(activity: ActivityEntity) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("activity", activity)
        startActivity(intent)
    }

    private fun showConfirmationDialog(activity: ActivityEntity) {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { dialog, _ ->
                GlobalScope.launch {
                    activityDao.deleteActivity(activity)
                    updateActivityList()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No", null)
            .show()
    }
}