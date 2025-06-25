package com.yjsoft.led.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.BitmapFactory
import android.widget.Toast
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.utils.YJZipUtils
import com.yjsoft.led.util.ShowCmdUtil
import androidx.fragment.app.Fragment
import com.yjsoft.led.R
import com.yjsoft.led.databinding.FragmentOperationBinding

class OperationFragment : Fragment() {
    private var _binding: FragmentOperationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val t1 = getString(R.string.text_slot1)
        val t2 = getString(R.string.text_slot2)
        val t3 = getString(R.string.text_slot3)
        val t4 = getString(R.string.text_slot4)

        binding.btnText1.text = t1
        binding.btnText2.text = t2
        binding.btnText3.text = t3
        binding.btnText4.text = t4

        binding.btnText1.setOnClickListener { sendText(t1) }
        binding.btnText2.setOnClickListener { sendText(t2) }
        binding.btnText3.setOnClickListener { sendText(t3) }
        binding.btnText4.setOnClickListener { sendText(t4) }

        binding.btnImage1.setOnClickListener { sendImage("images/img1.jpg") }
        binding.btnImage2.setOnClickListener { sendImage("images/img2.jpg") }
        binding.btnImage3.setOnClickListener { sendImage("images/img3.jpg") }
        binding.btnImage4.setOnClickListener { sendImage("images/img4.jpg") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun sendText(text: String) {
        val json = ShowCmdUtil.textMessage(text)
        YJDeviceManager.instance.sendShowCommon(json)
    }

    private fun sendImage(assetPath: String) {
        try {
            requireContext().assets.open(assetPath).use { stream ->
                if (assetPath.endsWith(".gif", true)) {
                    val bytes = stream.readBytes()
                    val zip = YJZipUtils.zipGif(bytes)
                    YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.gif(zip))
                } else {
                    val bitmap = BitmapFactory.decodeStream(stream)
                    binding.previewImage.setImageBitmap(bitmap)
                    val zip = YJZipUtils.zipPicture(bitmap)
                    YJDeviceManager.instance.sendShowCommon(ShowCmdUtil.picture(zip))
                }
            }
        } catch (e: Exception) {
            showToast(assetPath)
        }
    }
}
