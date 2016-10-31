package com.supermario.gobang.di

import android.content.Context
import com.supermario.gobang.startpage.MainActivity
import dagger.Component

/**
 * Created by lijia8 on 2016/10/18.
 */
@Component(modules = arrayOf(BaseDIModule::class))
interface BaseDIComponent {
    fun inject(activity: MainActivity)
    fun context(): Context
}