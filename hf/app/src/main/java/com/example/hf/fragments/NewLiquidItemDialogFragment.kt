package com.example.hf.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.hf.R
import com.example.hf.data.LiquidItem
import com.example.hf.databinding.DialogNewLiquidItemBinding

class NewLiquidItemDialogFragment(private var date: String) : DialogFragment(){
    interface NewLiquidItemDialogListener {
        fun onLiquidItemCreated(newItem: LiquidItem)
    }

    private lateinit var listener: NewLiquidItemDialogListener
    private lateinit var binding: DialogNewLiquidItemBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewLiquidItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewLiquidItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewLiquidItemBinding.inflate(LayoutInflater.from(context))
        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.category_items)
        )
        binding.tpTime.setIs24HourView(true)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_liquid_item)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok, null)
            .setNegativeButton(R.string.button_cancel, null)
        val alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (isValid()) {
                listener.onLiquidItemCreated(getLiquidItem())
                alertDialog.dismiss()
            } else {
                binding.etAmount.requestFocus()
                binding.etAmount.error = "Please enter the amount"
            }
        }

        return alertDialog
    }

    companion object {
        const val TAG = "NewLiquidItemDialogFragment"
    }

    private fun isValid() = binding.etAmount.text.isNotEmpty()

    private fun getLiquidItem() = LiquidItem(
        category = LiquidItem.Category.getByOrdinal(binding.spCategory.selectedItemPosition)
            ?: LiquidItem.Category.WATER,
        amount = binding.etAmount.text.toString().toInt(),
        time = binding.tpTime.hour.toString() + ":" + binding.tpTime.minute.toString(),
        date = date
    )
}