package com.example.storyapp.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText : androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Tidak diperlukan untuk validasi
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan untuk validasi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputLayout = parent.parent as? TextInputLayout
                if (s.isNullOrEmpty()) {
                    inputLayout?.error = "Password tidak boleh kosong"
                } else if (s.length < 8) {
                    inputLayout?.error = "Password minimal 8 karakter"
                } else {
                    inputLayout?.error = null // Hapus error jika valid
                }
            }
        })
    }
}