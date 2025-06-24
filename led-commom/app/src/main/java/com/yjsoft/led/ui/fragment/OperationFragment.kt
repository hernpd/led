package com.yjsoft.led.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yjsoft.led.R
import com.yjsoft.led.databinding.FragmentOperationBinding

class OperationFragment : Fragment() {
    private var _binding: FragmentOperationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnText1.setOnClickListener { showToast("text1") }
        binding.btnText2.setOnClickListener { showToast("text2") }
        binding.btnText3.setOnClickListener { showToast("text3") }
        binding.btnText4.setOnClickListener { showToast("text4") }
        binding.btnImage1.setOnClickListener { showToast("image1") }
        binding.btnImage2.setOnClickListener { showToast("image2") }
        binding.btnImage3.setOnClickListener { showToast("image3") }
        binding.btnImage4.setOnClickListener { showToast("image4") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
