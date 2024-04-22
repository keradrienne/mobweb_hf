package com.example.hf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hf.MyValueFormatter
import com.example.hf.R
import com.example.hf.data.LiquidItem
import com.example.hf.databinding.ItemLiquidListBinding
import com.example.hf.fragments.ModifyLiquidDialogFragment

class LiquidAdapter(private val listener: LiquidItemClickListener) :
    RecyclerView.Adapter<LiquidAdapter.LiquidViewHolder>() {

    private val items = mutableListOf<LiquidItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LiquidViewHolder(
        ItemLiquidListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: LiquidViewHolder, position: Int) {
        val liquidItem = items[position]

        holder.binding.ivIcon.setImageResource(getImageResource(liquidItem.category))
        holder.binding.tvCategory.text = liquidItem.category.name
        holder.binding.tvAmount.text = MyValueFormatter().getFormattedValue(liquidItem.amount.toFloat())
        holder.binding.tvTime.text = liquidItem.time

        holder.binding.ibRemove.setOnClickListener{listener.onItemDeleted(liquidItem)}
        holder.binding.ibModify.setOnClickListener{
            val modifyDialog = ModifyLiquidDialogFragment(liquidItem)
            val fragmentManager = holder.itemView.context as AppCompatActivity
            modifyDialog.show(fragmentManager.supportFragmentManager, "ModifyDialog")
        }
    }

    @DrawableRes
    private fun getImageResource(category: LiquidItem.Category): Int {
        return when (category) {
            LiquidItem.Category.WATER -> R.drawable.ic_action_water
            LiquidItem.Category.COFFEE -> R.drawable.ic_action_coffee
            LiquidItem.Category.TEA -> R.drawable.ic_action_tea
        }
    }
    override fun getItemCount(): Int = items.size

    interface LiquidItemClickListener {
        fun onLiquidItemChanged(item: LiquidItem)
        fun onItemDeleted(item: LiquidItem)
    }

    inner class LiquidViewHolder(val binding: ItemLiquidListBinding) : RecyclerView.ViewHolder(binding.root)

    fun addItem(item: LiquidItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(shoppingItems: List<LiquidItem>) {
        items.clear()
        items.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    fun remove(item: LiquidItem){
        items.remove(item)
        notifyDataSetChanged()
    }
}