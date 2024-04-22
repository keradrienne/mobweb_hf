package com.example.hf.fragments

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.hf.data.LiquidItem
import com.example.hf.databinding.DialogModifyLiquidItemBinding

class ModifyLiquidDialogFragment(private var liquidItem: LiquidItem) : DialogFragment() {
    interface ModifyLiquidItemDialogListener {
        fun onLiquidItemChanged(item: LiquidItem)
    }

    private lateinit var listener: ModifyLiquidItemDialogListener
    private lateinit var binding: DialogModifyLiquidItemBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? ModifyLiquidItemDialogListener
            ?: throw RuntimeException("Activity must implement the ModifyLiquidItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogModifyLiquidItemBinding.inflate(LayoutInflater.from(context))
        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(com.example.hf.R.array.category_items)
        )
        binding.spCategory.setSelection(LiquidItem.Category.toInt(liquidItem.category))
        binding.etAmount.setText(liquidItem.amount.toString())
        val hour = liquidItem.time.split(":")[0].toInt()
        val minute = liquidItem.time.split(":")[1].toInt()
        binding.tpTime.setIs24HourView(true)
        binding.tpTime.hour = hour
        binding.tpTime.minute = minute

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(com.example.hf.R.string.modify_liquid_item)
            .setView(binding.root)
            .setPositiveButton(com.example.hf.R.string.button_ok, null)
            .setNegativeButton(com.example.hf.R.string.button_cancel, null)
        val alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (isValid()) {
                listener.onLiquidItemChanged(getLiquidItem())
                alertDialog.dismiss()
            } else {
                binding.etAmount.requestFocus()
                binding.etAmount.error = "Please enter the amount"
            }
        }
        return alertDialog
    }

    private fun isValid() = binding.etAmount.text.isNotEmpty()

    private fun getLiquidItem() = LiquidItem(
        id = liquidItem.id,
        category = LiquidItem.Category.getByOrdinal(binding.spCategory.selectedItemPosition)
            ?: LiquidItem.Category.WATER,
        amount = binding.etAmount.text.toString().toInt(),
        time = binding.tpTime.hour.toString() + ":" + binding.tpTime.minute.toString(),
        date = liquidItem.date
    )
}