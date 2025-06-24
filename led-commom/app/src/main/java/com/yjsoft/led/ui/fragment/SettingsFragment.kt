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
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_wifi.setOnClickListener {
            startActivity(Intent(requireContext(), WifiActivity::class.java))
        }
        tv_ble.setOnClickListener {
            startActivity(Intent(requireContext(), BleActivity::class.java))
        }
    }
}
