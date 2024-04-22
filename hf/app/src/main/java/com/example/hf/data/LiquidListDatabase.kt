package com.example.hf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LiquidItem::class], version = 1)
@TypeConverters(value = [LiquidItem.Category::class])
abstract class LiquidListDatabase : RoomDatabase() {
    abstract fun liquidItemDao(): LiquidItemDao

    companion object {
        fun getDatabase(applicationContext: Context): LiquidListDatabase {
            return Room.databaseBuilder(
                applicationContext,
                LiquidListDatabase::class.java,
                "liquid-list"
            ).build()
        }
    }
}