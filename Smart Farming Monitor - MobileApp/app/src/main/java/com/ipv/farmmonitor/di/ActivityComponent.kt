package com.ipv.farmmonitor.di

import com.ipv.farmmonitor.LoginActivity
import com.ipv.farmmonitor.MainActivity
import com.ipv.farmmonitor.fragments.Home
import com.ipv.farmmonitor.fragments.Item
import com.ipv.farmmonitor.fragments.Alerts
import dagger.Subcomponent


@Subcomponent
interface ActivityComponent {

    // Activities
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    // Fragments
    fun inject(fragment: Home)
    fun inject(fragment: Item)
    fun inject(fragment: Alerts)
}

