package com.ipv.farmmonitor.adapter

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.ipv.farmmonitor.R
import com.ipv.farmmonitor.fragments.Item
import com.ipv.farmmonitor.models.Plantation
import kotlinx.android.synthetic.main.card_item.view.*


class PlantationsAdapter(
    private var data: MutableList<Plantation>,
    private var ids: ArrayList<String>
) : RecyclerView.Adapter<PlantationsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var card: MaterialCardView = view.findViewById(R.id.card)
        var healthy: TextView = view.findViewById(R.id.health)
        var switchWaterPump: TextView  = view.findViewById(R.id.switchWaterPump)
        var plantationId: TextView  = view.findViewById(R.id.plantID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val plantation: Plantation = data[position]
        holder.name.text = plantation.name
        holder.plantationId.text = plantation.id
        holder.healthy.text = setHealthy(plantation)

        val waterSystem: String = if(plantation.automaticWaterOn)
            "On"
        else
            "Off"
        holder.switchWaterPump.text = waterSystem

        when (plantation.type) {
            "Tomatoes" -> holder.card.cardImage.setImageResource(R.drawable.tomatoes)
            "Lettuces" -> holder.card.cardImage.setImageResource(R.drawable.lettuce_farm)
            "Cucumber" -> holder.card.cardImage.setImageResource(R.drawable.cucumber)
            "Beetroot" -> holder.card.cardImage.setImageResource(R.drawable.beetroot)
            "Cabbages" -> holder.card.cardImage.setImageResource(R.drawable.couves)
            "Corn" -> holder.card.cardImage.setImageResource(R.drawable.corn)
            "Beans" -> holder.card.cardImage.setImageResource(R.drawable.beans)
            "Apples" -> holder.card.cardImage.setImageResource(R.drawable.apples)
            "Lentils" -> holder.card.cardImage.setImageResource(R.drawable.lentils)
            "Citrus" -> holder.card.cardImage.setImageResource(R.drawable.citrus)
            "Other" -> holder.card.cardImage.setImageResource(R.drawable.other)
            else -> holder.card.cardImage.setImageResource(R.drawable.other)
        }


        holder.card.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("plantation", plantation)

            val myFragment: Fragment = Item(ids[position])
            myFragment.arguments = bundle



            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.frame_layout, myFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setHealthy(data: Plantation) : String{
        if(data.readings.isNotEmpty()) {
            when (data.readings.last().moisture) {
                in 0.0f..19.9f -> {
                    return "Very Low"
                }
                in 20.0f..39.0f -> {
                    return "Normal"
                }
                in 40.0f..59.9f -> {
                    return "Good"
                }
                in 60.0f..79.0f -> {
                    return "Low"
                }
                else -> {
                    return "Very Low"
                }
            }
        }else
            return "Unknow"
    }


    override fun getItemCount(): Int {
        return data.size
    }
}
