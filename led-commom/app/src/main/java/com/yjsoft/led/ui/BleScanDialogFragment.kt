package com.yjsoft.led.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.core.controler.YJCallBack
import com.yjsoft.led.adapter.BleDeviceListAdapter
import com.yjsoft.led.databinding.ActivityScanBleBinding

class BleScanDialogFragment : DialogFragment(), YJCallBack {
    private var _binding: ActivityScanBleBinding? = null
    private val binding get() = _binding!!
    private val deviceList = arrayListOf<YJBleDevice>()
    private var deviceListAdapter: BleDeviceListAdapter? = null
    private var isScan = false
    private var clickPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityScanBleBinding.inflate(inflater, container, false)
        deviceListAdapter = BleDeviceListAdapter(requireContext(), deviceList)
        binding.rcBluetoothList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcBluetoothList.adapter = deviceListAdapter
        deviceListAdapter?.setOnItemClickListener(object : BleDeviceListAdapter.OnItemClickListener {
            override fun OnClickListener(position: Int) {
                if (!YJDeviceManager.instance.isConnect(deviceList[position])) {
                    clickPosition = position
                    YJDeviceManager.instance.connect(deviceList[position])
                } else dismiss()
            }
        })

        binding.buttonScan.setOnClickListener {
            isScan = !isScan
            if (isScan) {
                binding.buttonScan.text = "스캔 일시중지"
                YJDeviceManager.instance.scan()
            } else {
                binding.buttonScan.text = "스캔 시작"
                YJDeviceManager.instance.stopScan()
            }
        }
        YJDeviceManager.instance.setCallBack(this)
        YJDeviceManager.instance.scan()
        isScan = true
        binding.buttonScan.text = "스캔 중지"
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        YJDeviceManager.instance.stopScan()
        _binding = null
    }

    private fun checkDevice(mac: String): Boolean {
        return deviceList.any { it.mac == mac }
    }

    override fun onScanning(yjBleDevice: YJBleDevice) {
        activity?.runOnUiThread {
            if ((yjBleDevice.name?.startsWith("YS") == true ||
                    yjBleDevice.name?.startsWith("LED") == true) &&
                !checkDevice(yjBleDevice.mac)
            ) {
                deviceList.add(yjBleDevice)
                deviceListAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onScanStarted() {}
    override fun startConnect() {}
    override fun connectFail() {}
    override fun connectSuccess() {
        activity?.runOnUiThread { dismiss() }
    }
    override fun disConnected() {}
    override fun resultData(data: String, progress: Int, type: Int) {}
    override fun sendFail(code: Int) {}
}
