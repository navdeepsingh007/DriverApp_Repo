package com.seasia.driverapp.common

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.seasia.driverapp.R

object DialogUtil {
    fun showDialog(context: Context, dialogInterface: DialogsInterface, title: String, msg: String): Dialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(
                R.string.yes,
                { dialog, which ->
                    dialogInterface.onDialogConfirmAction(null, title)
                })
            .setNegativeButton(R.string.no, null)
            .setIcon(R.drawable.ic_alert_golden)
            .show()
    }
}