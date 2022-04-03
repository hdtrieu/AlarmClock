package vn.dongtrieu.app.tikiandroid.hometest

import android.content.res.Resources

val Number.dpF: Float
    get() = this.toFloat() * Resources.getSystem().displayMetrics.density