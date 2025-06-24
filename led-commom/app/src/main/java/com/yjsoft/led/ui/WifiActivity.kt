package com.yjsoft.led.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.core.controler.YJCallBack
import com.yjsoft.core.utils.YJZipUtils
import com.yjsoft.led.adapter.WifiAdapter
import com.yjsoft.led.bean.TypeBean
import com.yjsoft.led.bean.WifiPasswordBean
import com.yjsoft.led.util.ShowCmdUtil
import kotlinx.android.synthetic.main.activity_wifi.*
import java.io.ByteArrayOutputStream
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.content.Context
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import com.yjsoft.led.R
import kotlinx.android.synthetic.main.light_seekbar_layout.view.*
import kotlinx.android.synthetic.main.set_password_layout.view.*


class WifiActivity : AppCompatActivity(), YJCallBack {
    private var wifiAdapter: WifiAdapter? = null
    private var typeList = arrayListOf<TypeBean>()
    private var oldPassword = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        setTitle(R.string.wifi)

        YJDeviceManager.instance.isWiFiDevice()

        typeList.clear()
        typeList.add(TypeBean(1, getString(R.string.get_device_info)))
        typeList.add(TypeBean(2, getString(R.string.get_screen_info)))
        typeList.add(TypeBean(3, getString(R.string.get_brightness)))
        typeList.add(TypeBean(4, getString(R.string.clear_screen)))
        typeList.add(TypeBean(5, getString(R.string.get_wifi_password)))

        typeList.add(TypeBean(6, getString(R.string.text_only)))
        typeList.add(TypeBean(7, getString(R.string.one_char_one_color)))
        typeList.add(TypeBean(8, getString(R.string.two_char_one_color)))
        typeList.add(TypeBean(9, getString(R.string.one_line_one_color)))

        typeList.add(TypeBean(10, getString(R.string.text_bg_image)))
        typeList.add(TypeBean(11, getString(R.string.text_bg_gif)))

        typeList.add(TypeBean(12, getString(R.string.text_fg_image)))
        typeList.add(TypeBean(13, getString(R.string.text_fg_gif)))

        typeList.add(TypeBean(14, getString(R.string.picture)))
        typeList.add(TypeBean(15, getString(R.string.gif)))


        typeList.add(TypeBean(16, getString(R.string.combo_text_top_img_bottom)))
        typeList.add(TypeBean(17, getString(R.string.combo_text_top_gif_bottom)))
        typeList.add(TypeBean(18, getString(R.string.combo_text_top_img_bottom_colorful)))
        typeList.add(TypeBean(19, getString(R.string.combo_text_top_gif_bottom_colorful)))

        typeList.add(TypeBean(20, getString(R.string.combo_img_top_text_bottom)))
        typeList.add(TypeBean(21, getString(R.string.combo_gif_top_text_bottom)))
        typeList.add(TypeBean(22, getString(R.string.combo_img_top_text_bottom_colorful)))
        typeList.add(TypeBean(23, getString(R.string.combo_gif_top_text_bottom_colorful)))


        typeList.add(TypeBean(24, getString(R.string.combo_text)))
        typeList.add(TypeBean(25, getString(R.string.combo_multi_text)))

        typeList.add(TypeBean(26, getString(R.string.set_brightness)))
        typeList.add(TypeBean(27, getString(R.string.delete_program)))
        typeList.add(TypeBean(28, getString(R.string.set_password)))


        wifiAdapter = WifiAdapter(this, typeList)
        rc_type.layoutManager = LinearLayoutManager(this)
        rc_type.adapter = wifiAdapter

        wifiAdapter?.setOnButtonClickListener(object : WifiAdapter.OnButtonClickListener {
            override fun OnClickListener(position: Int) {
                tv_result.text = ""
                when (typeList[position].position) {
                    6 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.text)
                    7 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.one_text)
                    8 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.two_text)
                    9 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.line_text)
                    10 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_with_text)
                    11 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_gif_with_text)

                    12 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_with_text)
                    13 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_gif_with_text)
                    14 -> {
//                        YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.picture)

                          val fileStream = assets.open("source/保持车距.jpg")
                          val bitmap = BitmapFactory.decodeStream(fileStream)
                          val json = YJZipUtils.zipPicture(bitmap)
                          YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.picture(json))
                    }
                    15 -> {
                        val gifByteArray = gifByteArray()
                        val gif = YJZipUtils.zipGif(gifByteArray)
                        YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.gif(gif))
                    }


                    16 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomPicture)
                    17 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGif)
                    18 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomDazzleColor)
                    19 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGifDazzleColor)
                    20 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopPicture)
                    21 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGif)
                    22 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopDazzleColor)
                    23 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGIFDazzleColor)
                    24 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationText)
                    25 -> YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTexts)

                    26 -> showSetLightDialog()

                    27 -> {
                        val delList = arrayListOf<Int>()
                        delList.add(1)
                        YJDeviceManager.instance.deleteControl(delList)
                    }

                    28 -> {
                        if (oldPassword.isEmpty())
                            ToastShow(getString(R.string.please_get_wifi_password))
                        else showSetPasswordDialog()
                    }

                    else -> YJDeviceManager.instance.sendCommonCmd(typeList[position].position)
                }
            }
        })

        YJDeviceManager.instance.setCallBack(this)
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

    }

    @SuppressLint("SetTextI18n")
    override fun resultData(data: String, progress: Int,type: Int) {
        runOnUiThread {
            if (type == 5){
                val gson = Gson().fromJson<WifiPasswordBean>(data,WifiPasswordBean::class.java)
                oldPassword =
                    if (gson.ack.param_wifi.pwd.isNotEmpty())  gson.ack.param_wifi.pwd
                    else if (gson.ack.param_wifi.pwd_coe.isNotEmpty()) gson.ack.param_wifi.pwd_coe
                    else ""
            }
            Log.e("------wtf: ",data+"_"+type)
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

    @SuppressLint("InflateParams")
    private fun showSetPasswordDialog(){
        val view = LayoutInflater.from(this).inflate(R.layout.set_password_layout,null)
        val dialog = AlertDialog.Builder(this).setView(view).show()
        view?.tv_led_cancel?.setOnClickListener { dialog.dismiss() }
        view?.tv_led_confirm?.setOnClickListener {
//            dialog.dismiss()
//            YJDeviceManager.instance.resetPassword(getWifiSsid(),"")

                val manager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    dialog.currentFocus!!.windowToken,
                    HIDE_NOT_ALWAYS
                )

                if (view.et_old_password.text.toString().trim().isEmpty()){
                    ToastShow(getString(R.string.enter_old_password))
                    return@setOnClickListener
                }

                if (view.et_new_password.text.toString().trim().isEmpty()){
                    ToastShow(getString(R.string.enter_new_password))
                    return@setOnClickListener
                }

                if (view.et_new_password_again.text.toString().trim().isEmpty()){
                    ToastShow(getString(R.string.enter_confirm_password))
                    return@setOnClickListener
                }

                if (!view.et_new_password.text.toString().equals(view.et_new_password_again.text.toString())){
                    ToastShow(getString(R.string.new_password_mismatch))
                    return@setOnClickListener
                }

                if (!oldPassword.equals(view.et_old_password.text.toString().trim())){
                    ToastShow(getString(R.string.old_password_error))
                    return@setOnClickListener
                }

                if (view.et_old_password.text.toString().trim().length < 8 ||
                    view.et_new_password.text.toString().trim().length < 8 ||
                    view.et_new_password_again.text.toString().trim().length < 8){
                    ToastShow(getString(R.string.password_min_length))
                    return@setOnClickListener
                }


                if (view.et_old_password.text.toString().trim().equals(view.et_new_password.text.toString().trim())){
                    ToastShow(getString(R.string.new_password_same))
                    return@setOnClickListener
                }

                dialog.dismiss()
                YJDeviceManager.instance.resetPassword(getWifiSsid(),view.et_new_password_again.text.toString().trim())
        }
    }


    @SuppressLint("MissingPermission")
    public fun getWifiSsid(): String{
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        var ssid = wifiInfo.ssid
        val networkId = wifiInfo.networkId
        val configuredNetwork = wifiManager.configuredNetworks
        if (configuredNetwork != null) {
            for (i in configuredNetwork) {
                if (i.networkId == networkId) {
                    ssid = i.SSID
                    break
                }
            }
        }
        return if (ssid.startsWith("\"") && ssid.endsWith("\""))
            ssid.substring(1,ssid.length - 1)
        else ssid
    }

    private fun ToastShow(text: String){
        tv_result.text = text
        val toast = Toast.makeText(this,text,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }
}
