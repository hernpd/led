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
import com.yjsoft.led.databinding.ActivityWifiBinding
import com.yjsoft.led.databinding.LightSeekbarLayoutBinding
import com.yjsoft.led.databinding.SetPasswordLayoutBinding
import com.yjsoft.led.databinding.ProgressDialogLayoutBinding
import java.io.ByteArrayOutputStream
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.content.Context
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import com.yjsoft.led.R


class WifiActivity : AppCompatActivity(), YJCallBack {
    private lateinit var binding: ActivityWifiBinding
    private var wifiAdapter: WifiAdapter? = null
    private var typeList = arrayListOf<TypeBean>()
    private var oldPassword = ""
    private var progressDialog: AlertDialog? = null
    private var progressBinding: ProgressDialogLayoutBinding? = null
    private var currentSendDesc = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.wifi)

        YJDeviceManager.instance.isWiFiDevice()

        typeList.clear()
        typeList.add(TypeBean(1,"장치 정보 가져오기"))
        typeList.add(TypeBean(2,"장치 화면 정보 가져오기"))
        typeList.add(TypeBean(3,"장치 밝기 가져오기"))
        typeList.add(TypeBean(4,"화면 지우기"))
        typeList.add(TypeBean(5,"WiFi 비밀번호 가져오기"))

        typeList.add(TypeBean(6,"텍스트"))
        typeList.add(TypeBean(7,"한 글자 한 색"))
        typeList.add(TypeBean(8,"두 글자 한 색"))
        typeList.add(TypeBean(9,"한 줄 한 색"))

        typeList.add(TypeBean(10,"텍스트-배경 이미지"))
        typeList.add(TypeBean(11,"텍스트-배경 GIF"))

        typeList.add(TypeBean(12,"텍스트-전경 이미지"))
        typeList.add(TypeBean(13,"텍스트-전경 GIF"))

        typeList.add(TypeBean(14,"이미지"))
        typeList.add(TypeBean(15,"GIF"))


        typeList.add(TypeBean(16,"조합-위 텍스트 아래 이미지"))
        typeList.add(TypeBean(17,"조합-위 텍스트 아래 GIF"))
        typeList.add(TypeBean(18,"조합-위 텍스트 아래 이미지-화려한 글꼴"))
        typeList.add(TypeBean(19,"조합-위 텍스트 아래 GIF-화려한 글꼴"))

        typeList.add(TypeBean(20,"조합-위 이미지 아래 텍스트"))
        typeList.add(TypeBean(21,"조합-위 GIF 아래 텍스트"))
        typeList.add(TypeBean(22,"조합-위 이미지 아래 텍스트-화려한 글꼴"))
        typeList.add(TypeBean(23,"조합-위 GIF 아래 텍스트-화려한 글꼴"))


        typeList.add(TypeBean(24,"조합-텍스트"))
        typeList.add(TypeBean(25,"조합-다중 텍스트"))

        typeList.add(TypeBean(26,"밝기 설정"))
        typeList.add(TypeBean(27,"지정 프로그램 삭제"))
        typeList.add(TypeBean(28,"비밀번호 설정"))


        wifiAdapter = WifiAdapter(this, typeList)
        binding.rcType.layoutManager = LinearLayoutManager(this)
        binding.rcType.adapter = wifiAdapter

        wifiAdapter?.setOnButtonClickListener(object : WifiAdapter.OnButtonClickListener {
            override fun OnClickListener(position: Int) {
                binding.tvResult.text = ""
                when (typeList[position].position) {
                    6 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.startText(this@WifiActivity)) }
                    7 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.one_text) }
                    8 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.two_text) }
                    9 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.line_text) }
                    10 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_with_text) }
                    11 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_gif_with_text) }

                    12 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_with_text) }
                    13 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_gif_with_text) }
                    14 -> {
//                        YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.picture)
                        showProgress(typeList[position].name ?: "")
                        try {
                            assets.open("images/img1.jpg").use { stream ->
                                val bitmap = BitmapFactory.decodeStream(stream)
                                val json = YJZipUtils.zipPicture(bitmap)
                                YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.picture(json))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@WifiActivity, "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    15 -> {
                        showProgress(typeList[position].name ?: "")
                        try {
                            val gifByteArray = gifByteArray()
                            val gif = YJZipUtils.zipGif(gifByteArray)
                            YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.gif(gif))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@WifiActivity, "GIF을 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }


                    16 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomPicture) }
                    17 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGif) }
                    18 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomDazzleColor) }
                    19 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGifDazzleColor) }
                    20 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopPicture) }
                    21 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGif) }
                    22 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopDazzleColor) }
                    23 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGIFDazzleColor) }
                    24 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationText) }
                    25 -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTexts) }

                    26 -> showSetLightDialog()

                    27 -> { showProgress(typeList[position].name ?: "");
                        val delList = arrayListOf<Int>()
                        delList.add(1)
                        YJDeviceManager.instance.deleteControl(delList)
                    }

                    28 -> {
                        if (oldPassword.isEmpty())
                            ToastShow("먼저 WiFi 비밀번호를 가져오세요")
                        else showSetPasswordDialog()
                    }

                    else -> { showProgress(typeList[position].name ?: ""); YJDeviceManager.instance.sendCommonCmd(typeList[position].position) }
                }
            }
        })

        YJDeviceManager.instance.setCallBack(this)
    }

    private fun gifByteArray(): ByteArray {
        return try {
            assets.open("images/img1.jpg").use { stream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                var len: Int
                val b = ByteArray(1024)
                while (((stream.read(b)).also { len = it }) != -1) {
                    byteArrayOutputStream.write(b, 0, len)
                }
                byteArrayOutputStream.flush()
                byteArrayOutputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
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
            binding.tvResult.text = "수신 데이터:\n${data}\n전송 진행률:${progress}%"
            updateProgress(progress)
        }
    }

    override fun sendFail(code: Int) {
        runOnUiThread {
            binding.tvResult.text = "전송 실패: $code"
            progressBinding?.tvStatus?.text = "${currentSendDesc} 전송 실패: $code"
            progressDialog?.dismiss()
        }
    }

    private fun showProgress(desc: String) {
        if (progressBinding == null) {
            progressBinding = ProgressDialogLayoutBinding.inflate(layoutInflater)
            progressDialog = AlertDialog.Builder(this)
                .setView(progressBinding!!.root)
                .setCancelable(false)
                .create()
        }
        currentSendDesc = desc
        progressBinding?.tvStatus?.text = "${desc} 전송 중"
        progressBinding?.progressBar?.progress = 0
        progressDialog?.show()
    }

    private fun updateProgress(progress: Int) {
        progressBinding?.progressBar?.progress = progress
        if (progress >= 100) {
            progressBinding?.tvStatus?.text = "${currentSendDesc} 전송 완료"
            progressDialog?.dismiss()
        } else {
            progressBinding?.tvStatus?.text = "${currentSendDesc} 전송 중...${progress}%"
        }
    }

    @SuppressLint("InflateParams")
    private fun showSetLightDialog(){
        val dialogBinding = LightSeekbarLayoutBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).show()
        dialogBinding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.tvConfirm.setOnClickListener {
            dialog.dismiss()
            showProgress("밝기 설정")
            YJDeviceManager.instance.setLight(dialogBinding.seekbar.progress + 1)
        }
    }

    @SuppressLint("InflateParams")
    private fun showSetPasswordDialog(){
        val dialogBinding = SetPasswordLayoutBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).show()
        dialogBinding.tvLedCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.tvLedConfirm.setOnClickListener {
//            dialog.dismiss()
//            YJDeviceManager.instance.resetPassword(getWifiSsid(),"")

                val manager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    dialog.currentFocus!!.windowToken,
                    HIDE_NOT_ALWAYS
                )

                if (dialogBinding.etOldPassword.text.toString().trim().isEmpty()){
                    ToastShow("이전 비밀번호를 입력하세요")
                    return@setOnClickListener
                }

                if (dialogBinding.etNewPassword.text.toString().trim().isEmpty()){
                    ToastShow("새 비밀번호를 입력하세요")
                    return@setOnClickListener
                }

                if (dialogBinding.etNewPasswordAgain.text.toString().trim().isEmpty()){
                    ToastShow("비밀번호 확인을 입력하세요")
                    return@setOnClickListener
                }

                if (!dialogBinding.etNewPassword.text.toString().equals(dialogBinding.etNewPasswordAgain.text.toString())){
                    ToastShow("새 비밀번호와 확인 비밀번호가 일치하지 않습니다")
                    return@setOnClickListener
                }

                if (!oldPassword.equals(dialogBinding.etOldPassword.text.toString().trim())){
                    ToastShow("이전 비밀번호가 틀렸습니다")
                    return@setOnClickListener
                }

                if (dialogBinding.etOldPassword.text.toString().trim().length < 8 ||
                    dialogBinding.etNewPassword.text.toString().trim().length < 8 ||
                    dialogBinding.etNewPasswordAgain.text.toString().trim().length < 8){
                    ToastShow("비밀번호는 최소 8자 이상입니다")
                    return@setOnClickListener
                }


                if (dialogBinding.etOldPassword.text.toString().trim().equals(dialogBinding.etNewPassword.text.toString().trim())){
                    ToastShow("새 비밀번호가 이전 비밀번호와 같습니다")
                    return@setOnClickListener
                }

                dialog.dismiss()
                showProgress("비밀번호 설정")
                YJDeviceManager.instance.resetPassword(getWifiSsid(),dialogBinding.etNewPasswordAgain.text.toString().trim())
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
        binding.tvResult.text = text
        val toast = Toast.makeText(this,text,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }
}
