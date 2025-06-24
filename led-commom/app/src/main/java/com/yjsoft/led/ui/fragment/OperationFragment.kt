package com.yjsoft.led.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yjsoft.led.R
import kotlinx.android.synthetic.main.fragment_operation.*

class OperationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_operation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_text1.setOnClickListener { showToast("text1") }
        btn_text2.setOnClickListener { showToast("text2") }
        btn_text3.setOnClickListener { showToast("text3") }
        btn_text4.setOnClickListener { showToast("text4") }
        btn_image1.setOnClickListener { showToast("image1") }
        btn_image2.setOnClickListener { showToast("image2") }
        btn_image3.setOnClickListener { showToast("image3") }
        btn_image4.setOnClickListener { showToast("image4") }
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
