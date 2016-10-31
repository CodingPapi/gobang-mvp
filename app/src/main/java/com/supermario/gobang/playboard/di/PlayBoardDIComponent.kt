package com.supermario.gobang.playboard.di

import com.supermario.gobang.playboard.PlayActivity
import dagger.Component

/**
 * Created by lijia8 on 2016/10/24.
 */
@Component(modules = arrayOf(PlayBoardDIModule::class))
interface PlayBoardDIComponent {

    fun inject(activity: PlayActivity)

}