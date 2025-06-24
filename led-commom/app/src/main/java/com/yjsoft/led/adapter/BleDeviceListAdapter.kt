package com.yjsoft.led.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.yjsoft.core.bean.YJBleDevice
import com.yjsoft.led.R

class BleDeviceListAdapter(
    private val context: Context,
    private val deviceList: List<YJBleDevice>
) : RecyclerView.Adapter<BleDeviceListAdapter.BleDeviceListHolder>() {
    private var mac: String? = null
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setMac(mac: String) {
        this.mac = mac
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceListHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_ble_device_layout, parent, false)
        return BleDeviceListHolder(view)
    }

    override fun onBindViewHolder(holder: BleDeviceListHolder, position: Int) {
        holder.tvMac.text = deviceList[position].mac
        holder.tvName.text = deviceList[position].name

        if (deviceList[position].mac == mac)
            holder.ivBle.visibility = View.VISIBLE
        else
            holder.ivBle.visibility = View.GONE

        holder.clBle.setOnClickListener { onItemClickListener!!.OnClickListener(position) }

    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    inner class BleDeviceListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvName: TextView
        internal var tvMac: TextView
        internal var ivBle: ImageView
        internal var clBle: ConstraintLayout

        init {

            tvName = itemView.findViewById(R.id.tv_device_name)
            tvMac = itemView.findViewById(R.id.tv_device_mac)
            ivBle = itemView.findViewById(R.id.iv_ble)
            clBle = itemView.findViewById(R.id.cl_ble)
        }
    }

    interface OnItemClickListener {
        fun OnClickListener(position: Int)
    }
}
