package vn.dongtrieu.app.tikiandroid.hometest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui.ClockActivity
import vn.dongtrieu.app.tikiandroid.hometest.gps.MainActivity

class SelectTestActivivy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_test_activivy)

        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, ClockActivity::class.java))
        }
    }
}