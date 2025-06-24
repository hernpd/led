package com.yjsoft.led.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yjsoft.led.R
import com.yjsoft.led.ui.BleActivity
import com.yjsoft.led.ui.WifiActivity
import com.yjsoft.led.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvWifi.setOnClickListener {
            startActivity(Intent(requireContext(), WifiActivity::class.java))
        }
        binding.tvBle.setOnClickListener {
            startActivity(Intent(requireContext(), BleActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
