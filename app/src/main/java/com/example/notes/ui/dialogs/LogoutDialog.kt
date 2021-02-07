package com.example.notes.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.notes.R

class LogoutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.logout_dialog_title))
            .setMessage(getString(R.string.logout_dialog_message))
            .setPositiveButton(getString(R.string.logout_dlg_ok_btn_title)) { _, _ ->
                (activity as LogoutListener).onLogout()
            }
            .setNegativeButton(getString(R.string.logout_dlg_no_btn_title)) { _, _ ->
                dismiss()
            }
            .create()

    interface LogoutListener {
        fun onLogout()
    }

    companion object {
        val TAG = "LOGOUT_DIALOG"
        fun createInstance() = LogoutDialog()
    }
}