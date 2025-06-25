package com.yjsoft.led.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.core.controler.YJCallBack
import com.yjsoft.led.adapter.BleDeviceListAdapter
import com.yjsoft.led.databinding.DialogBleScanBinding

class BleScanDialogFragment : DialogFragment(), YJCallBack {
    private var _binding: DialogBleScanBinding? = null
    private val binding get() = _binding!!

    private val deviceList = arrayListOf<YJBleDevice>()
    private var adapter: BleDeviceListAdapter? = null
    private var selectIndex = -1
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBleScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = BleDeviceListAdapter(requireContext(), deviceList)
        binding.rcBleDevices.layoutManager = LinearLayoutManager(requireContext())
        binding.rcBleDevices.adapter = adapter
        adapter?.setOnItemClickListener(object : BleDeviceListAdapter.OnItemClickListener {
            override fun OnClickListener(position: Int) {
                selectIndex = position
                adapter?.setMac(deviceList[position].mac)
                adapter?.notifyDataSetChanged()
            }
        })
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvConnect.setOnClickListener {
            if (selectIndex >= 0) {
                showProgressDialog("연결 중...")
                YJDeviceManager.instance.connect(deviceList[selectIndex])
            } else {
                Toast.makeText(requireContext(), "장치를 선택하세요", Toast.LENGTH_SHORT).show()
            }
        }
        YJDeviceManager.instance.setCallBack(this)
    }

    override fun onStart() {
        super.onStart()
        YJDeviceManager.instance.scan()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        YJDeviceManager.instance.stopScan()
        _binding = null
    }

    override fun onScanning(yjBleDevice: YJBleDevice) {
        activity?.runOnUiThread {
            if (deviceList.none { it.mac == yjBleDevice.mac }) {
                deviceList.add(yjBleDevice)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onScanStarted() {
        activity?.runOnUiThread {
            deviceList.clear()
            adapter?.notifyDataSetChanged()
        }
    }

    override fun startConnect() {}

    override fun connectFail() {
        activity?.runOnUiThread {
            hideProgressDialog()
            Toast.makeText(requireContext(), "연결 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun connectSuccess() {
        activity?.runOnUiThread {
            hideProgressDialog()
            dismiss()
        }
    }

    override fun disConnected() {}

    override fun resultData(data: String, progress: Int, type: Int) {}

    override fun sendFail(code: Int) {}

    private fun showProgressDialog(message: String) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(requireContext(), null, message, true, true)
        } else {
            progressDialog?.setMessage(message)
        }
        progressDialog?.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }
}
