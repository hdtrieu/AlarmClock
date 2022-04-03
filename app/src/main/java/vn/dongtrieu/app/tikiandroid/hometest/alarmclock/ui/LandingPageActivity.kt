package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import vn.dongtrieu.app.tikiandroid.hometest.R

class LandingPageActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
    }

    companion object {
        fun launchIntent(context: Context?): Intent {
            val i = Intent(
                context,
                LandingPageActivity::class.java
            )
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            return i
        }
    }

}