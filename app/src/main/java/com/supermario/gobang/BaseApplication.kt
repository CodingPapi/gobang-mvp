package com.supermario.gobang

import android.app.Application
import com.supermario.gobang.di.BaseDIComponent
import com.supermario.gobang.di.BaseDIModule
import com.supermario.gobang.di.DaggerBaseDIComponent

/**
 * Created by lijia8 on 2016/10/18.
 */
class BaseApplication : Application() {
    companion object {
        lateinit var baseDIComponent: BaseDIComponent
    }

    override fun onCreate() {
        super.onCreate()
        baseDIComponent = DaggerBaseDIComponent.builder().baseDIModule(BaseDIModule(applicationContext)).build()

    }
}