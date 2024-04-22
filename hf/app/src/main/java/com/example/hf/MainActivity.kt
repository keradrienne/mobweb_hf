package com.example.hf

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.hf.adapter.LiquidAdapter
import com.example.hf.data.LiquidItem
import com.example.hf.data.LiquidListDatabase
import com.example.hf.databinding.ActivityMainBinding
import com.example.hf.fragments.NewLiquidItemDialogFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), LiquidAdapter.LiquidItemClickListener,
    NewLiquidItemDialogFragment.NewLiquidItemDialogListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var database: LiquidListDatabase

    private val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            val title = "Main"
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

        loadLiquids()
    }

    override fun onLiquidItemChanged(item: LiquidItem) { }

    override fun onLiquidItemCreated(newItem: LiquidItem) {
        thread {
            val insertId = database.liquidItemDao().insert(newItem)
            newItem.id = insertId
            runOnUiThread {
                loadLiquids()
            }
        }
    }

    override fun onItemDeleted(item: LiquidItem){ }

    private fun loadLiquids(){
        lifecycleScope.launch {
            val waterAmount = getTotalAmount(LiquidItem.Category.WATER)
            val coffeeAmount = getTotalAmount(LiquidItem.Category.COFFEE)
            val teaAmount = getTotalAmount(LiquidItem.Category.TEA)
            val pieEntries = listOf(
                PieEntry(waterAmount, "Water"),
                PieEntry(coffeeAmount, "Coffee"),
                PieEntry(teaAmount, "Tea")
            )
            val dataSet = PieDataSet(pieEntries, "")
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueFormatter = MyValueFormatter()
            dataSet.colors = listOf(Color.parseColor("#00a9cf"),
                Color.parseColor("#744700"), Color.parseColor("#8fce00"))
            val data = PieData(dataSet)
            data.setDrawValues(true)
            binding.chartLiquid.description.isEnabled = false
            binding.chartLiquid.dragDecelerationFrictionCoef = 0.9f
            binding.chartLiquid.animateY(1400, Easing.EaseInOutQuad)
            binding.chartLiquid.data = data
            binding.chartLiquid.invalidate()
        }
    }

    private suspend fun getTotalAmount(category: LiquidItem.Category) : Float{
        return withContext(Dispatchers.IO) {
            database.liquidItemDao().getTotalAmountByCategory(category).toFloat()
        }
    }
}

class MyValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = "${value.toInt()} ml"
}