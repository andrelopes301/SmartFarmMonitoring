package com.ipv.farmmonitor.models

import java.io.Serializable

data class Notification(
    var title: String = "",
    var body: String = "",
    var type: String = "",
    var time: Long = 0,

) : Serializable






