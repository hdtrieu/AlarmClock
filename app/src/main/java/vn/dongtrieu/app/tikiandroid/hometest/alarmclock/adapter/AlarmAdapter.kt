package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.dongtrieu.app.tikiandroid.hometest.R
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.getAmPm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.getReadableTime
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui.EditAlarmActivity
import vn.dongtrieu.app.tikiandroid.hometest.bind

class AlarmAdapter:
    RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    private var mAlarms: List<Alarm> = emptyList()

    private lateinit var mDays: Array<String>
    private var mAccentColor = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.alarm_row, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAlarm(alarms: ArrayList<Alarm>) {
        mAlarms = alarms.toList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = holder.itemView.context
        mDays = c.resources.getStringArray(R.array.days_abbreviated)

        if (mAccentColor == -1) {
            mAccentColor = ContextCompat.getColor(c, R.color.accent)
        }

        val alarm: Alarm = mAlarms[position]

        holder.time.text = getReadableTime(alarm.time)
        holder.amPm.text = getAmPm(alarm.time)
        holder.label.text = alarm.label
        holder.days.text = buildSelectedDays(alarm)

        holder.itemView.setOnClickListener { view ->
            val launchEditAlarmIntent: Intent =
                EditAlarmActivity.buildAddEditAlarmActivityIntent(
                    view.context, EditAlarmActivity.EDIT_ALARM
                )
            launchEditAlarmIntent.putExtra(EditAlarmActivity.ALARM_EXTRA, alarm)
            view.context.startActivity(launchEditAlarmIntent)
        }
    }

    override fun getItemCount(): Int {
        return mAlarms.size
    }

    private fun buildSelectedDays(alarm: Alarm): Spannable {
        val numDays = 7
        val days = alarm.days
        val builder = SpannableStringBuilder()
        var span: ForegroundColorSpan
        var startIndex: Int
        var endIndex: Int
        for (i in 0 until numDays) {
            startIndex = builder.length
            val dayText: String = mDays[i]
            builder.append(dayText)
            builder.append(" ")
            endIndex = startIndex + dayText.length
            val isSelected = days.valueAt(i)
            if (isSelected) {
                span = ForegroundColorSpan(mAccentColor)
                builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return builder
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView by view.bind(R.id.ar_time)
        val amPm: TextView by view.bind(R.id.ar_am_pm)
        val label: TextView by view.bind(R.id.ar_label)
        val days: TextView by view.bind(R.id.ar_days)
    }

}