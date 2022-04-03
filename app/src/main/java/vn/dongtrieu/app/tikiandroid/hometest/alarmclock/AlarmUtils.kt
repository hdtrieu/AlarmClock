package vn.dongtrieu.app.tikiandroid.hometest.alarmclock

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.core.app.ActivityCompat
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.data.DatabaseHelper.*
import java.text.SimpleDateFormat
import java.util.*

private val TIME_FORMAT = SimpleDateFormat("h:mm", Locale.getDefault())
private val AM_PM_FORMAT = SimpleDateFormat("a", Locale.getDefault())

private const val REQUEST_ALARM = 1
private val PERMISSIONS_ALARM = arrayOf(
    Manifest.permission.VIBRATE
)

fun checkAlarmPermissions(activity: Activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return
    }

    val permission: Int = ActivityCompat.checkSelfPermission(
        activity, Manifest.permission.VIBRATE
    )

    if (permission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            activity,
            PERMISSIONS_ALARM,
            REQUEST_ALARM
        )
    }
}

fun toContentValues(alarm: Alarm): ContentValues {
    val cv = ContentValues(10)

    cv.put(COL_TIME, alarm.time)
    cv.put(COL_LABEL, alarm.label)

    val days = alarm.days
    cv.put(COL_MON, if (days[Alarm.MON]) 1 else 0)
    cv.put(COL_TUES, if (days[Alarm.TUES]) 1 else 0)
    cv.put(COL_WED, if (days[Alarm.WED]) 1 else 0)
    cv.put(COL_THURS, if (days[Alarm.THURS]) 1 else 0)
    cv.put(COL_FRI, if (days[Alarm.FRI]) 1 else 0)
    cv.put(COL_SAT, if (days[Alarm.SAT]) 1 else 0)
    cv.put(COL_SUN, if (days[Alarm.SUN]) 1 else 0)

    cv.put(COL_IS_ENABLED, alarm.isEnabled)

    return cv
}

@SuppressLint("Range")
fun buildAlarmList(c: Cursor?): ArrayList<Alarm> {
    if (c == null) return ArrayList()

    val size = c.count

    val alarms = ArrayList<Alarm>(size)

    if (c.moveToFirst()) {
        do {
            val id = c.getLong(c.getColumnIndex(_ID))
            val time = c.getLong(c.getColumnIndex(COL_TIME))
            val label = c.getString(c.getColumnIndex(COL_LABEL))
            val mon = c.getInt(c.getColumnIndex(COL_MON)) == 1
            val tues = c.getInt(c.getColumnIndex(COL_TUES)) == 1
            val wed = c.getInt(c.getColumnIndex(COL_WED)) == 1
            val thurs = c.getInt(c.getColumnIndex(COL_THURS)) == 1
            val fri = c.getInt(c.getColumnIndex(COL_FRI)) == 1
            val sat = c.getInt(c.getColumnIndex(COL_SAT)) == 1
            val sun = c.getInt(c.getColumnIndex(COL_SUN)) == 1
            val isEnabled = c.getInt(c.getColumnIndex(COL_IS_ENABLED)) == 1
            val alarm = Alarm(id, time, label)
            alarm.setDay(Alarm.MON, mon)
            alarm.setDay(Alarm.TUES, tues)
            alarm.setDay(Alarm.WED, wed)
            alarm.setDay(Alarm.THURS, thurs)
            alarm.setDay(Alarm.FRI, fri)
            alarm.setDay(Alarm.SAT, sat)
            alarm.setDay(Alarm.SUN, sun)
            alarm.setIsEnabled(isEnabled)
            alarms.add(alarm)
        } while (c.moveToNext())
    }

    return alarms
}

fun getReadableTime(time: Long): String {
    return TIME_FORMAT.format(time)
}

fun getAmPm(time: Long): String {
    return AM_PM_FORMAT.format(time)
}

fun isAlarmActive(alarm: Alarm): Boolean {
    val days = alarm.days
    var isActive = false
    var count = 0
    while (count < days.size() && !isActive) {
        isActive = days.valueAt(count)
        count++
    }
    return isActive
}