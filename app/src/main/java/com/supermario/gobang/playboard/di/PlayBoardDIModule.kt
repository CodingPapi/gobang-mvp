package com.supermario.gobang.playboard.di

import com.supermario.gobang.data.BaseData
import com.supermario.gobang.playboard.PlayView
import dagger.Module
import dagger.Provides

/**
 * Created by lijia8 on 2016/10/24.
 */
@Module
class PlayBoardDIModule(val view: PlayView, val data: BaseData) {

    @Provides
    fun providesView(): PlayView = view

    @Provides
    fun providesData(): BaseData = data
}