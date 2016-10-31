package com.supermario.gobang.data

import android.graphics.Color
import android.graphics.Point

/**
 * Created by lijia8 on 2016/10/19.
 */
data class Chess(var color: Int, var x: Int, var y: Int, var index: Int) {
    object COLOR {
        val WHITE = Color.GRAY
        val BLACK = Color.BLACK
    }
    override fun equals(other: Any?): Boolean {
        if (other is Chess) {
            return other.x == x && other.y == y
        } else {
            return false
        }
    }
}