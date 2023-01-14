package com.ipv.farmmonitor.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(val app: Application) {
    @Provides
    fun app() = app

    @Provides
    fun context() = app as Context

    @Provides
    fun resources(): Resources = app.resources

}