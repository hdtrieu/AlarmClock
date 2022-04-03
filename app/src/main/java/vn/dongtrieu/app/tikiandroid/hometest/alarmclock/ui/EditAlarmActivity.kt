package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IntDef
import androidx.appcompat.app.AppCompatActivity
import vn.dongtrieu.app.tikiandroid.hometest.R
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service.LoadAlarmsService
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.data.DatabaseHelper

class EditAlarmActivity: AppCompatActivity() {

    companion object {
        const val ALARM_EXTRA = "alarm_extra"
        const val MODE_EXTRA = "mode_extra"

        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @IntDef(EDIT_ALARM, ADD_ALARM, UNKNOWN)
        annotation class Mode

        const val EDIT_ALARM = 1
        const val ADD_ALARM = 2
        const val UNKNOWN = 0

        fun buildAddEditAlarmActivityIntent(
            context: Context,
            @Mode
            mode: Int
        ): Intent {
            val i = Intent(
                context,
                EditAlarmActivity::class.java
            )
            i.putExtra(MODE_EXTRA, mode)
            return i
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alarm)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getToolbarTitle()
        val alarm: Alarm = getAlarm()
        if (supportFragmentManager.findFragmentById(R.id.edit_alarm_frag_container) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.edit_alarm_frag_container, AddEditAlarmFragment.newInstance(alarm))
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    @Mode
    private fun getMode(): Int {
        return intent.getIntExtra(
            MODE_EXTRA,
            UNKNOWN
        )
    }

    private fun getToolbarTitle(): String {
        val titleResId: Int = when (getMode()) {
            EDIT_ALARM -> R.string.edit_alarm
            ADD_ALARM -> R.string.add_alarm
            UNKNOWN -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        EditAlarmActivity::class.java.simpleName + " must match value"
            )
            else -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        EditAlarmActivity::class.java.simpleName + " must match value"
            )
        }
        return getString(titleResId)
    }

    private fun getAlarm(): Alarm {
        when (getMode()) {
            EDIT_ALARM -> return intent.getParcelableExtra(ALARM_EXTRA)!!
            ADD_ALARM -> {
                val id: Long = DatabaseHelper.getInstance(this).addAlarm()
                LoadAlarmsService.enqueueWork(this)
                return Alarm(id)
            }
            UNKNOWN -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        EditAlarmActivity::class.java.simpleName + " must match value"
            )
            else -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        EditAlarmActivity::class.java.simpleName + " must match value"
            )
        }
    }
}