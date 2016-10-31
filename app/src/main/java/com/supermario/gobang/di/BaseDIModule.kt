package com.supermario.gobang.di

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Created by lijia8 on 2016/10/18.
 */
@Module
class BaseDIModule(val context: Context) {

    @Provides
    fun provideContext(): Context = context

}