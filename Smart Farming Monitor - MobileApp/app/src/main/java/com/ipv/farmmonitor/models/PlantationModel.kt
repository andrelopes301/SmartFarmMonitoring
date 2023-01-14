package com.ipv.farmmonitor.models

import java.io.Serializable

data class Plantation(
    var id: String = "",
    var name: String = "",
    var type: String = "",
    var notificationsOn: Boolean = false,
    var automaticWaterOn: Boolean = false,
    var waterOn: Boolean = false,
    var readings: ArrayList<Reading> = arrayListOf()
) : Serializable{
    constructor() : this("", "","", true, readings = arrayListOf() )
}

data class Reading(
    var id: String = "",
    var temperature: Float = 0f,
    var humidity: Float = 0f,
    var moisture: Float = 0f,
    var light: Float = 0f,
    var time: Long = 0,
) : Serializable







