package com.maritza.thundertools.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maritza.thundertools.databinding.ItemModuleActionBinding
import com.maritza.thundertools.domain.ModuleType

data class UiActionItem(val type: ModuleType, val label: String, val commandKey: String, val requiresOnline: Boolean)

class ModuleAdapter(
    private val items: List<UiActionItem>,
    private val onClick: (UiActionItem) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.VH>() {

    class VH(val b: ItemModuleActionBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemModuleActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.title.text = item.label
        holder.b.module.text = item.type.name
        holder.b.online.text = if (item.requiresOnline) "Online" else "Offline"
        holder.b.card.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}