package com.yjsoft.led.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.yjsoft.led.databinding.ProgressDialogLayoutBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.core.controler.YJCallBack
import com.yjsoft.core.utils.*
import com.yjsoft.led.R
import com.yjsoft.led.adapter.BleAdapter
import com.yjsoft.led.adapter.WifiAdapter
import com.yjsoft.led.util.ShowCmdUtil
import com.yjsoft.led.databinding.ActivityBleBinding
import java.io.ByteArrayOutputStream

class BleActivity : AppCompatActivity(), YJCallBack {
    private lateinit var binding: ActivityBleBinding
    private var bleAdapter: BleAdapter? = null
    private val typeList = arrayListOf<String>()
    private var progressDialog: AlertDialog? = null
    private var progressBinding: ProgressDialogLayoutBinding? = null
    private var currentSendDesc = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.bluetooth)

        typeList.add("블루투스 목록")
        typeList.add("장치 정보 가져오기")
        typeList.add("장치 화면 정보 가져오기")
        typeList.add("장치 밝기 가져오기")
        typeList.add("화면 지우기")

        typeList.add("텍스트")
        typeList.add("한 글자 한 색")
        typeList.add("두 글자 한 색")
        typeList.add("한 줄 한 색")

        typeList.add("텍스트-배경 이미지")
        typeList.add("텍스트-배경 GIF")

        typeList.add("텍스트-전경 이미지")
        typeList.add("텍스트-전경 GIF")

        typeList.add("이미지")
        typeList.add("GIF")


        typeList.add("조합-위 텍스트 아래 이미지")
        typeList.add("조합-위 텍스트 아래 GIF")
        typeList.add("조합-위 텍스트 아래 이미지-화려한 글꼴")
        typeList.add("조합-위 텍스트 아래 GIF-화려한 글꼴")

        typeList.add("조합-위 이미지 아래 텍스트")
        typeList.add("조합-위 GIF 아래 텍스트")
        typeList.add("조합-위 이미지 아래 텍스트-화려한 글꼴")
        typeList.add("조합-위 GIF 아래 텍스트-화려한 글꼴")


        typeList.add("조합-텍스트")
        typeList.add("조합-다중 텍스트")

        typeList.add("밝기 설정")
        typeList.add("지정 프로그램 삭제")
        typeList.add("연결 해제")

        bleAdapter = BleAdapter(this,typeList)
        binding.rcBle.layoutManager = LinearLayoutManager(this)
        binding.rcBle.adapter = bleAdapter

        bleAdapter?.setOnButtonClickListener(object : BleAdapter.OnButtonClickListener{
            override fun OnClickListener(position: Int) {
                binding.tvResult.text = ""
                when(position){
                    0 -> {
                        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                            Toast.makeText(this@BleActivity,"블루투스를 켜주세요",Toast.LENGTH_SHORT).show()
                            BluetoothAdapter.getDefaultAdapter().enable()
                        }else startActivity(Intent(this@BleActivity,ScanBleActivity::class.java))
                    }

                    5 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.startText(this@BleActivity)) }
                    6 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.one_text) }
                    7 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.two_text) }
                    8 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.line_text) }
                    9 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_with_text) }
                    10 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.background_gif_with_text) }

                    11 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_with_text) }
                    12 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.prospects_gif_with_text) }
                    13 -> {
                        showProgress(typeList[position])
                        try {
                            assets.open("images/img1.jpg").use { stream ->
                                val bitmap = BitmapFactory.decodeStream(stream)
                                val json = YJZipUtils.zipPicture(bitmap)
                                YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.picture(json))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@BleActivity, "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    14 -> {
                        showProgress(typeList[position])
                        try {
                            val gifByteArray = gifByteArray()
                            val gif = YJZipUtils.zipGif(gifByteArray)
                            YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.gif(gif))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@BleActivity, "GIF을 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }

                    15 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomPicture) }
                    16 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGif) }
                    17 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomDazzleColor) }
                    18 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationBottomGifDazzleColor) }
                    19 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopPicture) }
                    20 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGif) }
                    21 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopDazzleColor) }
                    22 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTopGIFDazzleColor) }
                    23 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationText) }
                    24 -> { showProgress(typeList[position]); YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.combinationTexts) }

                    25 -> {
                        // Brightness control moved to OperationFragment
                    }
                    26 -> {
                        showProgress(typeList[position])
                        val delList = arrayListOf<Int>()
                        delList.add(1)
                        YJDeviceManager.instance.deleteControl(delList)
                    }
                    27 ->{
                        showProgress(typeList[position])
                        YJDeviceManager.instance.disconnect()
                    }
                    else -> { showProgress(typeList[position]); YJDeviceManager.instance.sendCommonCmd(position) }
                }
            }
        })
    }

    private fun gifByteArray(): ByteArray {
        return try {
            val path = if (assetExists("images/img1.gif")) "images/img1.gif" else "images/img1.jpg"
            assets.open(path).use { stream ->
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
            binding.tvResult.text = "장치 연결 해제"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun resultData(data: String, progress: Int,type: Int) {
        Log.e("--线程：",Thread.currentThread().name)
        runOnUiThread {
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

    private fun assetExists(path: String): Boolean {
        return try {
            assets.open(path).close()
            true
        } catch (_: Exception) {
            false
        }
    }

}
