package com.yjsoft.led.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

import com.yjsoft.led.R
import com.yjsoft.led.bean.TypeBean

class BleAdapter(private val context: Context, private val list: List<String>) :
    RecyclerView.Adapter<BleAdapter.WifiViewHolder>() {
    private var onButtonClickListener: OnButtonClickListener? = null

    fun setOnButtonClickListener(onButtonClickListener: OnButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false)
        return WifiViewHolder(view)
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        holder.button.text = list[position]
        holder.button.setOnClickListener {
            onButtonClickListener?.OnClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var button: Button = itemView.findViewById(R.id.wifi_bottom)
    }

    interface OnButtonClickListener {
        fun OnClickListener(position: Int)
    }
}
