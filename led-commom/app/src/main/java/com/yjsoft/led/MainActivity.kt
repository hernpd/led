package com.yjsoft.led

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import com.yjsoft.led.ui.ScanBleActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.led.ui.fragment.OperationFragment
import com.yjsoft.led.ui.fragment.SettingsFragment
import com.yjsoft.led.ui.fragment.StatusFragment
import com.yjsoft.led.util.FileUtils
import com.yjsoft.led.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val requestCode = 0x600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_ble) {
            startActivity(Intent(this, ScanBleActivity::class.java))
            true
        } else {
            super.onOptionsItemSelected(item)
        }
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

        val denied = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (denied.isEmpty()) {
            YJDeviceManager.instance.init(this.application)
        } else {
            ActivityCompat.requestPermissions(this, denied.toTypedArray(), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                YJDeviceManager.instance.init(this.application)
            } else {
                finish()
            }
        }
    }

    private fun initFontDir() {
        val file = File(externalCacheDir?.absolutePath.plus("/Led/.font/"))
        Log.e("------路径：", file.absolutePath)
        if (!file.exists()) {
            file.mkdirs()
            FileUtils.copyAssetsFiles(this, "font", file.absolutePath)
        }
    }
}
