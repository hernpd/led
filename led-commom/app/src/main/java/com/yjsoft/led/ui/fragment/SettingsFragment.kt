package com.yjsoft.led.ui.fragment

import android.content.Intent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.yjsoft.led.util.SettingsUtils
import com.yjsoft.core.utils.YJUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yjsoft.led.R
import com.yjsoft.led.ui.BleActivity
import com.yjsoft.led.ui.WifiActivity
import com.yjsoft.led.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SettingsUtils.applyScreenSize(requireContext())
        binding.tvWifi.setOnClickListener {
            startActivity(Intent(requireContext(), WifiActivity::class.java))
        }
        binding.tvBle.setOnClickListener {
            startActivity(Intent(requireContext(), BleActivity::class.java))
        }

        binding.btnStartMessage.setOnClickListener {
            val edit = EditText(requireContext())
            edit.setText(SettingsUtils.getStartMessage(requireContext()))
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.update_start_message)
                .setView(edit)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    SettingsUtils.setStartMessage(requireContext(), edit.text.toString())
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        binding.btnScreenSize.setOnClickListener {
            val options = arrayOf("64 x 64", "128 x 96")
            val checked = if (SettingsUtils.getScreenWidth(requireContext()) == 128) 1 else 0
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.select_screen_size)
                .setSingleChoiceItems(options, checked) { dialog, which ->
                    val (w, h) = if (which == 0) 64 to 64 else 128 to 96
                    SettingsUtils.setScreenSize(requireContext(), w, h)
                    YJUtils.setScreenWidth(w)
                    YJUtils.setScreenHeight(h)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
