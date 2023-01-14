package com.ipv.farmmonitor

import android.app.Application
import android.content.res.Resources
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.farmmonitor.di.ApplicationComponent
import com.ipv.farmmonitor.di.ApplicationModule
import com.ipv.farmmonitor.di.DaggerApplicationComponent
import io.realm.kotlin.mongodb.App
import dagger.Module
import dagger.Provides

lateinit var realmApp: App

inline fun <reified T> T.TAG(): String = T::class.java.simpleName


open class SmartFarmingApp : Application() {

    companion object {
        lateinit var instance: Application
        lateinit var resourses: Resources
    }

    open val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }



    override fun onCreate() {
        super.onCreate()
        instance = this
        resourses = resources
        realmApp = App.create(getString(R.string.realm_app_id))
        Log.v(TAG(), "Initialized the Realm App configuration for: ${realmApp.configuration.appId}")
    }
}
