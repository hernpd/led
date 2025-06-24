package com.yjsoft.led.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yjsoft.led.R
import com.yjsoft.led.databinding.FragmentStatusBinding

class StatusFragment : Fragment() {
    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatus()
    }

    private fun updateStatus() {
        binding.tvWifiStatus.text = getConnectionStatus(NetworkCapabilities.TRANSPORT_WIFI)
        binding.tvBluetoothStatus.text = getString(R.string.status_unknown)
        binding.tvLedStatus.text = getString(R.string.status_unknown)
    }

    private fun getConnectionStatus(transport: Int): String {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return getString(R.string.status_disconnected)
        val caps = cm.getNetworkCapabilities(network) ?: return getString(R.string.status_disconnected)
        return if (caps.hasTransport(transport)) {
            getString(R.string.status_connected)
        } else {
            getString(R.string.status_disconnected)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
