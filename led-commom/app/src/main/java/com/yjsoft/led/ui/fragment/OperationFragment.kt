package com.yjsoft.led.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Movie
import java.io.ByteArrayInputStream
import android.widget.Toast
import android.widget.SeekBar
import android.widget.ImageButton
import com.yjsoft.core.YJDeviceManager
import com.yjsoft.core.utils.YJZipUtils
import com.yjsoft.led.util.ShowCmdUtil
import com.yjsoft.led.util.SettingsUtils
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
        SettingsUtils.applyScreenSize(requireContext())
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
            val gifPath = "images/img${index + 1}.gif"
            val jpgPath = "images/img${index + 1}.jpg"
            val assetManager = requireContext().assets
            val path = if (assetExists(assetManager, gifPath)) gifPath else jpgPath

            try {
                assetManager.open(path).use { stream ->
                    val bitmap = if (path.endsWith(".gif")) {
                        decodeGifFirstFrame(stream.readBytes())
                    } else {
                        BitmapFactory.decodeStream(stream)
                    }
                    bitmap?.let { button.setImageBitmap(it) }
                }
            } catch (_: Exception) {
            }

            button.setOnClickListener {
                selectButton(button)
                sendImage(path)
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
                val bytes = stream.readBytes()
                if (assetPath.endsWith(".gif", true)) {
                    val bitmap = decodeGifFirstFrame(bytes)
                    bitmap?.let { binding.previewImage.setImageBitmap(it) }
                    val zip = YJZipUtils.zipGif(bytes)
                    if (bitmap != null) {
                        YJDeviceManager.instance.sendShowCommon(
                            ShowCmdUtil.gif(zip, bitmap.width, bitmap.height)
                        )
                    }
                } else {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.previewImage.setImageBitmap(bitmap)
                    val zip = YJZipUtils.zipPicture(bitmap)
                    YJDeviceManager.instance.sendShowCommon(
                        ShowCmdUtil.picture(zip, bitmap.width, bitmap.height)
                    )
                }
            }
        } catch (e: Exception) {
            showToast(assetPath)
        }
    }

    private fun assetExists(assetManager: android.content.res.AssetManager, path: String): Boolean {
        return try {
            assetManager.open(path).close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun decodeGifFirstFrame(bytes: ByteArray): Bitmap? {
        return try {
            val movie = Movie.decodeByteArray(bytes, 0, bytes.size) ?: return null
            val bitmap = Bitmap.createBitmap(movie.width(), movie.height(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            movie.setTime(0)
            movie.draw(canvas, 0f, 0f)
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
