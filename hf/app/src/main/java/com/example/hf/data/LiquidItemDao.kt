package com.example.hf.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LiquidItemDao {
    @Query("SELECT * FROM liquiditem")
    fun getAll(): List<LiquidItem>

    @Insert
    fun insert(liquidItems: LiquidItem): Long

    @Update
    fun update(liquidItem: LiquidItem)

    @Delete
    fun deleteItem(liquidItem: LiquidItem)

    @Query("SELECT SUM(amount), date FROM liquiditem WHERE category = :selectedCategory AND date = DATE('now')")
    fun getTotalAmountByCategory(selectedCategory: LiquidItem.Category): Long

    @Query("SELECT * FROM liquiditem WHERE date = :selectedDate")
    fun getAllByDate(selectedDate: String): List<LiquidItem>
}