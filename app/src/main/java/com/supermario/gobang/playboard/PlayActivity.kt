package com.supermario.gobang.playboard

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.supermario.gobang.R
import com.supermario.gobang.data.BaseData
import com.supermario.gobang.data.Data
import com.supermario.gobang.playboard.di.DaggerPlayBoardDIComponent
import com.supermario.gobang.playboard.di.PlayBoardDIModule
import javax.inject.Inject

class PlayActivity : AppCompatActivity(), PlayFragment.OnFragmentInteractionListener {

    @Inject
    lateinit var presenter: PlayPresenterImpl

    lateinit var data: BaseData

    override fun onFragmentInteraction(uri: Uri) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        var fragment: PlayFragment? = supportFragmentManager.findFragmentById(R.id.fragment_container) as PlayFragment?
        if (fragment == null) {
            fragment = PlayFragment.newInstance("", "")
            replaceFragment(fragment, R.id.fragment_container)
        }

        data = Data()

        //create presenter
        DaggerPlayBoardDIComponent.builder().playBoardDIModule(PlayBoardDIModule(fragment, data)).build().inject(this)


    }

    fun replaceFragment(fragment: Fragment, resId: Int) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}
