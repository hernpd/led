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
import kotlinx.android.synthetic.main.fragment_status.*

class StatusFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatus()
    }

    private fun updateStatus() {
        tv_wifi_status.text = getConnectionStatus(NetworkCapabilities.TRANSPORT_WIFI)
        tv_bluetooth_status.text = getString(R.string.status_unknown)
        tv_led_status.text = getString(R.string.status_unknown)
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
}
