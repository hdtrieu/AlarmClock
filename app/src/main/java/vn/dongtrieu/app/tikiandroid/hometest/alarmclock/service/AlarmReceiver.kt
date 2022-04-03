package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service

import android.app.*
import android.app.AlarmManager.AlarmClockInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import vn.dongtrieu.app.tikiandroid.hometest.R
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.isAlarmActive
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui.LandingPageActivity.Companion.launchIntent
import java.util.*

class AlarmReceiver: BroadcastReceiver() {

    companion object {
        private val TAG = AlarmReceiver::class.java.simpleName
        private const val CHANNEL_ID = "alarm_channel"

        private const val BUNDLE_EXTRA = "bundle_extra"
        private const val ALARM_KEY = "alarm_key"

        private fun createNotificationChannel(ctx: Context) {
            if (VERSION.SDK_INT < VERSION_CODES.O) return
            val mgr = ctx.getSystemService(NotificationManager::class.java)
                ?: return
            val name = ctx.getString(R.string.channel_name)
            if (mgr.getNotificationChannel(name) == null) {
                val channel =
                    NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000, 500, 1000, 500, 1000, 500)
                channel.setBypassDnd(true)
                mgr.createNotificationChannel(channel)
            }
        }

        private fun launchAlarmLandingPage(ctx: Context, alarm: Alarm): PendingIntent? {
            return PendingIntent.getActivity(
                ctx, alarm.notificationId(), launchIntent(ctx), PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun setReminderAlarms(context: Context, alarms: List<Alarm?>) {
            for (alarm in alarms) {
                if (alarm != null) {
                    setReminderAlarm(context, alarm)
                }
            }
        }

        fun setReminderAlarm(context: Context, alarm: Alarm) {
            if (!isAlarmActive(alarm)) {
                cancelReminderAlarm(context, alarm)
                return
            }
            val nextAlarmTime: Calendar = getTimeForNextAlarm(alarm)
            alarm.time = nextAlarmTime.timeInMillis
            val intent = Intent(context, AlarmReceiver::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ALARM_KEY, alarm)
            intent.putExtra(BUNDLE_EXTRA, bundle)
            val pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            ScheduleAlarm.with(context).schedule(alarm, pIntent)
        }

        fun cancelReminderAlarm(context: Context, alarm: Alarm) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(pIntent)
        }

        private fun getTimeForNextAlarm(alarm: Alarm): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = alarm.time
            val currentTime = System.currentTimeMillis()
            val startIndex: Int = getStartIndexFromTime(calendar)
            var count = 0
            var isAlarmSetForDay: Boolean
            val daysArray = alarm.days
            do {
                val index = (startIndex + count) % 7
                isAlarmSetForDay = daysArray.valueAt(index) && calendar.timeInMillis > currentTime
                if (!isAlarmSetForDay) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    count++
                }
            } while (!isAlarmSetForDay && count < 7)
            return calendar
        }

        private fun getStartIndexFromTime(c: Calendar): Int {
            val dayOfWeek = c[Calendar.DAY_OF_WEEK]
            var startIndex = 0
            when (dayOfWeek) {
                Calendar.MONDAY -> startIndex = 0
                Calendar.TUESDAY -> startIndex = 1
                Calendar.WEDNESDAY -> startIndex = 2
                Calendar.THURSDAY -> startIndex = 3
                Calendar.FRIDAY -> startIndex = 4
                Calendar.SATURDAY -> startIndex = 5
                Calendar.SUNDAY -> startIndex = 6
            }
            return startIndex
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarm: Alarm? = intent.getBundleExtra(BUNDLE_EXTRA)!!.getParcelable(ALARM_KEY)
        if (alarm == null) {
            Log.e(TAG, "Alarm is null", NullPointerException())
            return
        }

        val id: Int = alarm.notificationId()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(context)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_alarm_white)
        builder.color = ContextCompat.getColor(context, R.color.accent)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(alarm.label)
        builder.setTicker(alarm.label)
        builder.setVibrate(longArrayOf(1000, 500, 1000, 500, 1000, 500))
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.setContentIntent(launchAlarmLandingPage(context, alarm))
        builder.setAutoCancel(true)
        builder.priority = Notification.PRIORITY_HIGH
        manager.notify(id, builder.build())
        setReminderAlarm(context, alarm)
    }

    private class ScheduleAlarm private constructor(
        private val am: AlarmManager,
        private val ctx: Context
    ) {
        fun schedule(alarm: Alarm, pi: PendingIntent) {
            if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
                am.setAlarmClock(
                    AlarmClockInfo(
                        alarm.time, launchAlarmLandingPage(
                            ctx, alarm
                        )
                    ), pi
                )
            } else
                am.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pi)
        }

        companion object {
            fun with(context: Context): ScheduleAlarm {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                return ScheduleAlarm(am, context)
            }
        }
    }
}