package vn.dongtrieu.app.tikiandroid.hometest

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

@Suppress("UNCHECKED_CAST")
fun <V : View?> Activity.bind(@IdRes idRes: Int) = lazy(LazyThreadSafetyMode.NONE) {
    findViewById<View>(idRes) as V
}

@Suppress("UNCHECKED_CAST")
fun <V : View?> Fragment.bind(@IdRes idRes: Int) = lazy(LazyThreadSafetyMode.NONE) {
    view!!.findViewById<View>(idRes) as V
}

@Suppress("UNCHECKED_CAST")
fun <T : View?> View.bind(@IdRes id: Int, initializer: (T.() -> Unit)? = null) = lazy(LazyThreadSafetyMode.NONE) {
    val view = this@bind.findViewById<View>(id) as T
    initializer?.invoke(view)
    view
}