package com.example.notes.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notes.R
import com.example.notes.databinding.ColorSelectionDialogBinding

class ColorSelectionDialog : DialogFragment() {

    var ui: ColorSelectionDialogBinding? = null
    var onColorSelectedListener: ((color: Int?) -> Unit)? = null
        set(value) {
            field = value
            ui?.colorPickerPalette?.onSelectedColorChangedListener = field
        }

    var selectedColor: Int? = null
        set(value) {
            field = value
            ui?.colorPickerPalette?.selectedColor = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ui = ColorSelectionDialogBinding.inflate(layoutInflater, container, false)
        return ui?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.dialog?.setTitle(getString(R.string.chose_note_color_dialog_title))

        ui?.colorPickerPalette?.onSelectedColorChangedListener = { color ->
            selectedColor = color
            onColorSelectedListener?.apply {
                this(color)
            }
        }

        ui?.colorPickerPalette?.selectedColor = selectedColor

        ui?.colorSelectionDialogCloseBtn?.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ui = null
    }
}