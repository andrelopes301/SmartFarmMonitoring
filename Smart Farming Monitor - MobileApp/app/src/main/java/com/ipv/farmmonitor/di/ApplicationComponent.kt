package com.ipv.farmmonitor.di
import com.ipv.farmmonitor.SmartFarmingApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class
    ]
)
interface ApplicationComponent {

    fun activityComponent(): ActivityComponent
    fun inject(app: SmartFarmingApp)
}