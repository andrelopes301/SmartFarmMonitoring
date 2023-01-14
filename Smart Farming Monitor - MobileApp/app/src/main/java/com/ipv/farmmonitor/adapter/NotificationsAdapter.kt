package com.ipv.farmmonitor.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipv.farmmonitor.R
import com.ipv.farmmonitor.models.Notification
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NotificationsAdapter(
    private var data: MutableList<Notification>,
) : RecyclerView.Adapter<NotificationsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.notificationImage)
        var title: TextView = view.findViewById(R.id.notificationTitle)
        var body: TextView = view.findViewById(R.id.notificationBody)
        var lastTimeUpdated: TextView = view.findViewById(R.id.lastTimeUpdated)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val notification: Notification = data[position]

        when(notification.type){
            "Alert" -> holder.image.setImageResource(R.drawable.red_alert_icon)
            "Info" ->  holder.image.setImageResource(R.drawable.info_icon)
            "Rain" ->  holder.image.setImageResource(R.drawable.rain_icon)
            else ->  holder.image.setImageResource(R.drawable.info_icon)
        }

        holder.title.text = notification.title
        holder.body.text = notification.body

        holder.lastTimeUpdated.text = getDate(notification.time)



    }

    private fun getDate(timestamp: Long): String {
        var dateTime = ""
        try {
            val df: DateFormat = SimpleDateFormat(" HH:mm  |  dd/MM/yyyy")
            val date = Date(timestamp * 1000)
            dateTime = df.format(date)
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.d("Exception", it) }
        }
        return dateTime
    }


    override fun getItemCount(): Int {
        return data.size
    }
}
