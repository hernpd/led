package com.yjsoft.led.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.core.controler.YJCallBack
import com.yjsoft.core.utils.*
import com.yjsoft.led.R
import com.yjsoft.led.adapter.BleAdapter
import com.yjsoft.led.adapter.WifiAdapter
import com.yjsoft.led.util.ShowCmdUtil
import kotlinx.android.synthetic.main.activity_ble.*
import kotlinx.android.synthetic.main.light_seekbar_layout.view.*
import java.io.ByteArrayOutputStream

class BleActivity : AppCompatActivity(), YJCallBack {
    private var bleAdapter: BleAdapter? = null
    private val typeList = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble)

        setTitle(R.string.bluetooth)

        typeList.add(getString(R.string.bluetooth_list))
        typeList.add(getString(R.string.get_device_info))
        typeList.add(getString(R.string.get_screen_info))
        typeList.add(getString(R.string.get_brightness))
        typeList.add(getString(R.string.clear_screen))

        typeList.add(getString(R.string.text_only))
        typeList.add(getString(R.string.one_char_one_color))
        typeList.add(getString(R.string.two_char_one_color))
        typeList.add(getString(R.string.one_line_one_color))

        typeList.add(getString(R.string.text_bg_image))
        typeList.add(getString(R.string.text_bg_gif))

        typeList.add(getString(R.string.text_fg_image))
        typeList.add(getString(R.string.text_fg_gif))

        typeList.add(getString(R.string.picture))
        typeList.add(getString(R.string.gif))


        typeList.add(getString(R.string.combo_text_top_img_bottom))
        typeList.add(getString(R.string.combo_text_top_gif_bottom))
        typeList.add(getString(R.string.combo_text_top_img_bottom_colorful))
        typeList.add(getString(R.string.combo_text_top_gif_bottom_colorful))

        typeList.add(getString(R.string.combo_img_top_text_bottom))
        typeList.add(getString(R.string.combo_gif_top_text_bottom))
        typeList.add(getString(R.string.combo_img_top_text_bottom_colorful))
        typeList.add(getString(R.string.combo_gif_top_text_bottom_colorful))


        typeList.add(getString(R.string.combo_text))
        typeList.add(getString(R.string.combo_multi_text))

        typeList.add(getString(R.string.set_brightness))
        typeList.add(getString(R.string.delete_program))
        typeList.add(getString(R.string.disconnect))

        bleAdapter = BleAdapter(this,typeList)
        rc_ble.layoutManager = LinearLayoutManager(this)
        rc_ble.adapter = bleAdapter

        bleAdapter?.setOnButtonClickListener(object : BleAdapter.OnButtonClickListener{
            override fun OnClickListener(position: Int) {
                tv_result.text = ""
                when(position){
                    0 -> {
                        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                            Toast.makeText(this@BleActivity, getString(R.string.enable_bluetooth), Toast.LENGTH_SHORT).show()
                            BluetoothAdapter.getDefaultAdapter().enable()
                        }else startActivity(Intent(this@BleActivity,ScanBleActivity::class.java))
                    }

                    5 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.text)
                    6 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.one_text)
                    7 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.two_text)
                    8 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.line_text)
                    9 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_with_text)
                    10 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_gif_with_text)

                    11 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_with_text)
                    12 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_gif_with_text)
                    13 -> {
                        val fileStream = assets.open("source/保持车距.jpg")
                        val bitmap = BitmapFactory.decodeStream(fileStream)
                        val json = YJZipUtils.zipPicture(bitmap)
                        YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.picture(json))
                    }
                    14 -> {
                        val gifByteArray = gifByteArray()
                        val gif = YJZipUtils.zipGif(gifByteArray)
                        YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.gif(gif))
                    }

                    15 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomPicture)
                    16 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGif)
                    17 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomDazzleColor)
                    18 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGifDazzleColor)
                    19 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopPicture)
                    20 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGif)
                    21 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopDazzleColor)
                    22 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGIFDazzleColor)
                    23 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationText)
                    24 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTexts)

                    25 -> showSetLightDialog()
                    26 -> {
                        val delList = arrayListOf<Int>()
                        delList.add(1)
                        YJDeviceManager.instance.deleteControl(delList)
                    }
                    27 ->{
                        YJDeviceManager.instance.disconnect()
                    }
                    else -> YJDeviceManager.instance.sendCommonCmd(position)
                }
            }
        })
    }

    private fun gifByteArray(): ByteArray{
        val stream = assets.open("source/保持车距.gif")
        val byteArrayOutputStream = ByteArrayOutputStream()
        var len: Int
        val b = ByteArray(1024)
        while (((stream.read(b)).also { len = it }) != -1) {
            byteArrayOutputStream.write(b, 0, len)
        }
        byteArrayOutputStream.flush()
        byteArrayOutputStream.close()

        return byteArrayOutputStream.toByteArray()
    }

    override fun onResume() {
        super.onResume()
        YJDeviceManager.instance.setCallBack(this)
    }


    override fun onScanning(yjBleDevice: YJBleDevice) {

    }

    override fun onScanStarted() {

    }

    override fun startConnect() {

    }

    override fun connectFail() {

    }

    override fun connectSuccess() {

    }

    override fun disConnected() {
        runOnUiThread {
            ScanBleActivity.yjBleDevice = null
            tv_result.text = getString(R.string.device_disconnected)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun resultData(data: String, progress: Int,type: Int) {
        Log.e("--线程：",Thread.currentThread().name)
        runOnUiThread {
            tv_result.text = String.format(getString(R.string.result_progress), data, progress)
        }
    }

    override fun sendFail(code: Int) {
        runOnUiThread {
            tv_result.text = String.format(getString(R.string.send_failed), code)
        }
    }

    @SuppressLint("InflateParams")
    private fun showSetLightDialog(){
        val view = LayoutInflater.from(this).inflate(R.layout.light_seekbar_layout,null)
        val dialog = AlertDialog.Builder(this).setView(view).show()
        view?.tv_cancel?.setOnClickListener { dialog.dismiss() }
        view?.tv_confirm?.setOnClickListener {
            dialog.dismiss()
            YJDeviceManager.instance.setLight(view.seekbar?.progress!! + 1)
        }
    }
}
