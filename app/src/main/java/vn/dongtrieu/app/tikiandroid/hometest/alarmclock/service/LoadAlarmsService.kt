package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.data.DatabaseHelper

class LoadAlarmsService: JobIntentService() {

    companion object {
        private val TAG = LoadAlarmsService::class.java.simpleName
        val ACTION_COMPLETE = "$TAG.ACTION_COMPLETE"
        const val ALARMS_EXTRA = "alarms_extra"

        fun enqueueWork(context: Context) {
            val launchLoadAlarmsServiceIntent = Intent(context, LoadAlarmsService::class.java)
            enqueueWork(context, LoadAlarmsService::class.java, 1, launchLoadAlarmsServiceIntent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val alarms: ArrayList<Alarm> = DatabaseHelper.getInstance(this).alarms
        val i = Intent(ACTION_COMPLETE)
        i.putParcelableArrayListExtra(ALARMS_EXTRA, alarms)
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }

}