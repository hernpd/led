package com.yjsoft.led

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.led.ui.fragment.OperationFragment
import com.yjsoft.led.ui.fragment.SettingsFragment
import com.yjsoft.led.ui.fragment.StatusFragment
import com.yjsoft.led.util.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFontDir()
        YJDeviceManager.instance.init(this.application)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_operation -> switchFragment(OperationFragment())
                R.id.navigation_status -> switchFragment(StatusFragment())
                R.id.navigation_settings -> switchFragment(SettingsFragment())
            }
            true
        }

        bottom_navigation.selectedItemId = R.id.navigation_operation
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
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
