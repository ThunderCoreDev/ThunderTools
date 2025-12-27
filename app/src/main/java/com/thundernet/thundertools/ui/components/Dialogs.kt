package com.maritza.thundertools.ui

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maritza.thundertools.databinding.DialogArgsBinding

object Dialogs {
    fun showActionDialog(
        context: Context,
        item: UiActionItem,
        onConfirm: (Map<String, String>) -> Unit
    ) {
        val b = DialogArgsBinding.inflate(LayoutInflater.from(context))
        val title = "Acción: ${item.label}"
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(b.root)
            .setPositiveButton("Ejecutar") { _, _ ->
                val args = mutableMapOf<String, String>()
                if (b.target.text?.isNotBlank() == true) args["target"] = b.target.text.toString()
                if (b.value.text?.isNotBlank() == true) args["value"] = b.value.text.toString()
                onConfirm(args)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}