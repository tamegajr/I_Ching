package br.com.tamegatech.iching

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment

class DialogWelcome : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(R.string.DialogTitle).setMessage(R.string.DialogMessage)
            .setCancelable(false)
            .setPositiveButton(
                R.string.DialogButtonOk
            ) { _, _ -> }

        return builder.create()
    }
}
