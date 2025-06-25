package com.yjsoft.led.ui

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.core.controler.YJCallBack
import com.yjsoft.led.R
import com.yjsoft.led.adapter.BleDeviceListAdapter
import com.yjsoft.led.databinding.ActivityScanBleBinding

class ScanBleActivity : AppCompatActivity(), YJCallBack {
    private lateinit var binding: ActivityScanBleBinding
    private val deviceList = arrayListOf<YJBleDevice>()
    private var deviceListAdapter: BleDeviceListAdapter? = null
    private var clickPosition = -1
    private var progressDialog: ProgressDialog? = null
    private var isScan = false
    companion object{
        var yjBleDevice: YJBleDevice? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.bluetooth_list)


        deviceListAdapter = BleDeviceListAdapter(this,deviceList)
        binding.rcBluetoothList.layoutManager = LinearLayoutManager(this)
        binding.rcBluetoothList.adapter = deviceListAdapter


        deviceListAdapter?.setOnItemClickListener(object : BleDeviceListAdapter.OnItemClickListener{
            override fun OnClickListener(position: Int) {
                if (!YJDeviceManager.instance.isConnect(deviceList[position])) {
                    showProgressDialog("연결 중...")
                    clickPosition = position
                    YJDeviceManager.instance.connect(deviceList[position])
                }else finish()
            }
        })

        YJDeviceManager.instance.setCallBack(this)

        checkPermission()

        binding.buttonScan.setOnClickListener {
            isScan = !isScan
            if (isScan){
                binding.buttonScan.text = "스캔 일시중지"
                checkPermission()
            }else{
                binding.buttonScan.text = "스캔 시작"
                YJDeviceManager.instance.stopScan()
            }
        }
    }

    private fun checkPermission() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        val permissionDeniedList = ArrayList<String>()
        for (permission in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionDeniedList.add(permission)
            }
        }

        if (permissionDeniedList.isEmpty()) {
            YJDeviceManager.instance.scan()
        } else {
            val deniedPermissions = permissionDeniedList.toTypedArray()
            ActivityCompat.requestPermissions(
                this,
                deniedPermissions,
                0x601
            )
        }
    }

    private var dialog: Dialog? = null
    private fun onPermissionGranted(permission: String) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialog?.dismiss()
                    dialog = AlertDialog.Builder(this)
                        /*.setTitle(getString(R.string.tip))
                        .setMessage(getString(R.string.please_open_gps))
                        .setNegativeButton(
                            getString(R.string.cancel)
                        ) { dialog, which ->
                            dialog?.dismiss()
                            finish()
                        }
                        .setPositiveButton(
                            getString(R.string.go_set)
                        ) { dialog, which ->
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS)
                        }*/
                        .setCancelable(false)
                        .show()
                }
            }
        }
    }

    override fun onScanning(yjBleDevice: YJBleDevice) {
        runOnUiThread {
            if ((yjBleDevice.name?.startsWith("YS") == true ||
                    yjBleDevice.name?.startsWith("LED") == true) &&
                !checkDevice(yjBleDevice.mac)) {
                deviceList.add(yjBleDevice)
                deviceListAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun checkDevice(mac: String): Boolean{
        var isExit = false
        for (i in deviceList){
            if (i.mac.equals(mac)){
                isExit = true
                break
            }
        }
        return isExit
    }

    override fun onScanStarted() {
        runOnUiThread {
            deviceList.clear()
            if (yjBleDevice != null){
                deviceList.add(yjBleDevice!!)
                deviceListAdapter?.setMac(yjBleDevice!!.mac)
                deviceListAdapter?.notifyDataSetChanged()
            }

            binding.buttonScan.text = "스캔 중지"
            isScan = true
        }
    }

    override fun startConnect() {

    }

    override fun connectFail() {
        runOnUiThread {
            hideProgressDialog()
            Toast.makeText(this, "연결 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun connectSuccess() {
        runOnUiThread {
            hideProgressDialog()
            if (clickPosition > -1)
                yjBleDevice = deviceList[clickPosition]

            Toast.makeText(this, "연결 성공", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun disConnected() {
        runOnUiThread {
            yjBleDevice = null
            deviceListAdapter?.setMac("")
            deviceListAdapter?.notifyDataSetChanged()
        }
    }

    override fun resultData(data: String, progress: Int,type: Int) {

    }

    override fun sendFail(code: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        YJDeviceManager.instance.stopScan()
    }

    private fun showProgressDialog(message: String){
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title,
                message, true, true);
//            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
        } else if (progressDialog?.isShowing!!) {
            progressDialog?.setTitle(title)
            progressDialog?.setMessage(message)
        }
        progressDialog?.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing!!) {
            progressDialog?.dismiss()
        }
    }
}
