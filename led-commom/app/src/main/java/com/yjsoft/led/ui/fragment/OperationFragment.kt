package com.yjsoft.led.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.BitmapFactory
import android.widget.Toast
import android.widget.SeekBar
import android.widget.ImageButton
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.utils.YJZipUtils
import com.yjsoft.led.util.ShowCmdUtil
import androidx.fragment.app.Fragment
import com.yjsoft.led.R
import com.yjsoft.led.databinding.FragmentOperationBinding

class OperationFragment : Fragment() {
    private var _binding: FragmentOperationBinding? = null
    private val binding get() = _binding!!
    private var selectedButton: ImageButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttons = listOf(
            binding.btnImage1,
            binding.btnImage2,
            binding.btnImage3,
            binding.btnImage4,
            binding.btnImage5,
            binding.btnImage6,
            binding.btnImage7,
            binding.btnImage8
        )

        buttons.forEachIndexed { index, button ->
            try {
                requireContext().assets.open("images/img${index + 1}.jpg").use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    button.setImageBitmap(bitmap)
                }
            } catch (_: Exception) {
            }
            button.setOnClickListener {
                selectButton(button)
                sendImage("images/img${index + 1}.jpg")
            }
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    YJDeviceManager.instance.setLight(progress + 1)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selectButton(button: ImageButton) {
        selectedButton?.isSelected = false
        selectedButton = button
        selectedButton?.isSelected = true
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
