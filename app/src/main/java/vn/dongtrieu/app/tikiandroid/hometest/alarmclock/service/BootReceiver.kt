package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service.AlarmReceiver.Companion.setReminderAlarms
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.data.DatabaseHelper
import java.util.concurrent.Executors

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.getAction()) {
            Executors.newSingleThreadExecutor().execute {
                val alarms: List<Alarm> = DatabaseHelper.getInstance(context).getAlarms()
                setReminderAlarms(context, alarms)
            }
        }
    }
}