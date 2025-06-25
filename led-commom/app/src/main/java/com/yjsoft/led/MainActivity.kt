package com.yjsoft.led

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.core.controler.YJCallBack
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.yjsoft.led.ui.fragment.OperationFragment
import com.yjsoft.led.ui.fragment.SettingsFragment
import com.yjsoft.led.ui.fragment.StatusFragment
import com.yjsoft.led.ui.BleScanDialogFragment
import com.yjsoft.led.util.FileUtils
import com.yjsoft.led.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), YJCallBack {
    private lateinit var binding: ActivityMainBinding
    private val requestCode = 0x600
    private var savedMac: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val prefs = getSharedPreferences("ble_device", MODE_PRIVATE)
        savedMac = prefs.getString("mac", null)

        initFontDir()
        checkPermission()

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_operation -> switchFragment(OperationFragment())
                R.id.navigation_status -> switchFragment(StatusFragment())
                R.id.navigation_settings -> switchFragment(SettingsFragment())
            }
            true
        }

        binding.bottomNavigation.selectedItemId = R.id.navigation_operation
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun checkPermission() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                YJDeviceManager.instance.init(this.application)
                checkAutoConnect()
            } else {
                showPermissionExplanation()
            }
        }
    }

    private fun showPermissionExplanation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required_title))
            .setMessage(getString(R.string.permission_required_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.action_open_settings)) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.action_retry)) { _, _ ->
                checkPermission()
            }
            .show()
    }

    private fun initFontDir() {
        val file = File(externalCacheDir?.absolutePath.plus("/Led/.font/"))
        Log.e("------路径：", file.absolutePath)
        if (!file.exists()) {
            file.mkdirs()
            FileUtils.copyAssetsFiles(this, "font", file.absolutePath)
        }
    }

    private fun checkAutoConnect() {
        if (savedMac != null) {
            YJDeviceManager.instance.setCallBack(this)
            YJDeviceManager.instance.scan()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ble -> {
                BleScanDialogFragment().show(supportFragmentManager, "bleScan")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onScanning(yjBleDevice: YJBleDevice) {
        if (savedMac != null && savedMac == yjBleDevice.mac) {
            YJDeviceManager.instance.connect(yjBleDevice)
        }
    }

    override fun onScanStarted() {}
    override fun startConnect() {}
    override fun connectFail() {}
    override fun connectSuccess() {}
    override fun disConnected() {}
    override fun resultData(data: String, progress: Int, type: Int) {}
    override fun sendFail(code: Int) {}
}
