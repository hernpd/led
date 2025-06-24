package com.yjsoft.led

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.led.ui.BleActivity
import com.yjsoft.led.ui.WifiActivity
import com.yjsoft.led.util.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val file = File(externalCacheDir?.absolutePath.plus("/Led/.font/"))
        Log.e("------路径：",file.absolutePath)
        if (!file.exists()){
            file.mkdirs()
            FileUtils.copyAssetsFiles(this,"font",file.absolutePath)
        }

        YJDeviceManager.instance.init(this.application)

        tv_wifi.setOnClickListener {
            startActivity(Intent(this, WifiActivity::class.java))
        }

        tv_ble.setOnClickListener {
            startActivity(Intent(this, BleActivity::class.java))
        }
    }
}
