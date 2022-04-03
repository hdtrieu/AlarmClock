package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm

class LoadAlarmReceiver constructor(
    listener: OnAlarmsLoadedListener
): BroadcastReceiver() {

    private val mListener: OnAlarmsLoadedListener = listener

    override fun onReceive(context: Context?, intent: Intent?) {
        val alarms: ArrayList<Alarm> =
            intent?.getParcelableArrayListExtra(LoadAlarmsService.ALARMS_EXTRA)!!
        mListener.onAlarmsLoaded(alarms)
    }

    interface OnAlarmsLoadedListener {
        fun onAlarmsLoaded(alarms: ArrayList<Alarm>)
    }
}