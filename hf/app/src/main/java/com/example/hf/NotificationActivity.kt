package com.example.hf

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import com.example.hf.databinding.ActivityNotificationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            val title = "Notifications"
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

        val sharedPrefs = getSharedPreferences("SwitchStates", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        binding.switchcompat1.isChecked = sharedPrefs.getBoolean("switch1_state", false)
        binding.switchcompat2.isChecked = sharedPrefs.getBoolean("switch2_state", false)
        binding.switchcompat3.isChecked = sharedPrefs.getBoolean("switch3_state", false)
        binding.switchcompat4.isChecked = sharedPrefs.getBoolean("switch4_state", false)
        binding.switchcompat5.isChecked = sharedPrefs.getBoolean("switch5_state", false)
        binding.switchcompat6.isChecked = sharedPrefs.getBoolean("switch6_state", false)
        binding.switchcompat7.isChecked = sharedPrefs.getBoolean("switch7_state", false)

        binding.switchcompat1.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switch1_state", isChecked).apply()
            handleAlarm(isChecked, 1, 8, 0)
        }

        binding.switchcompat2.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switch2_state", isChecked).apply()
            handleAlarm(isChecked, 2, 10, 0)
        }

        binding.switchcompat3.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switch3_state", isChecked).apply()
            handleAlarm(isChecked, 3, 12, 0)
        }

        binding.switchcompat4.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switch4_state", isChecked).apply()
            handleAlarm(isChecked, 4, 14, 0)
        }

        binding.switchcompat5.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switch5_state", isChecked).apply()
            handleAlarm(isChecked, 5, 16, 0)
        }

        binding.switchcompat6.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switch6_state", isChecked).apply()
            handleAlarm(isChecked, 6, 18, 0)
        }

        binding.switchcompat7.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switch7_state", isChecked).apply()
            handleAlarm(isChecked, 7, 20, 0)
        }
    }

    private fun handleAlarm(isChecked: Boolean, requestCode: Int, hour: Int, minute: Int) {
        val sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val previousState = sharedPreferences.getBoolean("switch$requestCode", false)

        if (isChecked && !previousState) {
            editor.putBoolean("switch$requestCode", isChecked)
            editor.putInt("hour$requestCode", hour)
            editor.putInt("minute$requestCode", minute)
            editor.apply()

            setAlarm(requestCode, hour, minute)
            val formattedMinute = String.format("%02d", minute)
            Toast.makeText(this, "Notification scheduled at $hour:$formattedMinute", Toast.LENGTH_SHORT).show()
        } else if (!isChecked && previousState) {
            editor.putBoolean("switch$requestCode", isChecked)
            editor.remove("hour$requestCode")
            editor.remove("minute$requestCode")
            editor.apply()

            cancelAlarm(requestCode)
            Toast.makeText(this, "Notification canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAlarm(requestCode: Int, hour: Int, minute: Int) {
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            // Biztosítja, hogy a beállított időpont mindig a jövőbeni időpontra vonatkozzon
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmMgr.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    private fun cancelAlarm(requestCode: Int) {
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
    }
}