package com.example.hf

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hf.adapter.LiquidAdapter
import com.example.hf.data.LiquidItem
import com.example.hf.data.LiquidListDatabase
import com.example.hf.databinding.ActivityDetailsBinding
import com.example.hf.fragments.ModifyLiquidDialogFragment
import com.example.hf.fragments.NewLiquidItemDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.concurrent.thread

class DetailsActivity : AppCompatActivity(), LiquidAdapter.LiquidItemClickListener,
    NewLiquidItemDialogFragment.NewLiquidItemDialogListener, ModifyLiquidDialogFragment.ModifyLiquidItemDialogListener {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var database: LiquidListDatabase
    private lateinit var adapter: LiquidAdapter

    private var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            val title = "Details"
            val textColor = Color.WHITE
            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(ForegroundColorSpan(textColor), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setTitle(spannableTitle)
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_main -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_details -> {
                    val intent = Intent(this, DetailsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_notification -> {
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        database = LiquidListDatabase.getDatabase(applicationContext)

        binding.fab.setOnClickListener {
            NewLiquidItemDialogFragment(date).show(
                supportFragmentManager,
                NewLiquidItemDialogFragment.TAG
            )
        }

        binding.datePicker.setOnDateChangedListener{ _, year, month, dayOfMonth->
            date = year.toString() + "-" + (month + 1).toString() + "-" + dayOfMonth.toString()
            loadItemsInBackground()
        }

        binding.datePicker.maxDate = Date().time

        initRecyclerView()
    }

    override fun onLiquidItemChanged(item: LiquidItem) {
        thread {
            database.liquidItemDao().update(item)
            loadItemsInBackground()
        }
        Toast.makeText(applicationContext, "Item modified", Toast.LENGTH_SHORT).show()
    }

    override fun onLiquidItemCreated(newItem: LiquidItem) {
        thread {
            val insertId = database.liquidItemDao().insert(newItem)
            newItem.id = insertId
            runOnUiThread {
                adapter.addItem(newItem)
                Toast.makeText(applicationContext, "Item added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        adapter = LiquidAdapter(this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.liquidItemDao().getAllByDate(date)
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemDeleted(item: LiquidItem){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { dialogInterface, _ ->
                thread {
                    database.liquidItemDao().deleteItem(item)
                    runOnUiThread {
                        adapter.remove(item)
                        Toast.makeText(applicationContext, "Item deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}