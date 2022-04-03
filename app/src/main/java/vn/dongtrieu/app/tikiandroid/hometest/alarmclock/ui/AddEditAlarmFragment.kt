package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import vn.dongtrieu.app.tikiandroid.hometest.R
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.getTimePickerHour
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.getTimePickerMinute
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service.AlarmReceiver
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service.LoadAlarmsService
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.setTimePickerTime
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.data.DatabaseHelper
import java.util.*

class AddEditAlarmFragment: Fragment() {

    companion object {
        fun newInstance(alarm: Alarm): AddEditAlarmFragment {
            val args = Bundle()
            args.putParcelable(EditAlarmActivity.ALARM_EXTRA, alarm)
            val fragment = AddEditAlarmFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mTimePicker: TimePicker
    private lateinit var mLabel: EditText
    private lateinit var mMon: CheckBox
    private lateinit var mTues: CheckBox
    private lateinit var mWed: CheckBox
    private lateinit var mThurs: CheckBox
    private lateinit var mFri: CheckBox
    private lateinit var mSat: CheckBox
    private lateinit var mSun: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_add_edit_alarm, container, false)
        setHasOptionsMenu(true)
        val alarm = getAlarm()

        mTimePicker = v.findViewById<View>(R.id.edit_alarm_time_picker) as TimePicker
        setTimePickerTime(mTimePicker, alarm.time)
        mLabel = v.findViewById<View>(R.id.edit_alarm_label) as EditText
        mLabel.setText(alarm.label)
        mMon = v.findViewById<View>(R.id.edit_alarm_mon) as CheckBox
        mTues = v.findViewById<View>(R.id.edit_alarm_tues) as CheckBox
        mWed = v.findViewById<View>(R.id.edit_alarm_wed) as CheckBox
        mThurs = v.findViewById<View>(R.id.edit_alarm_thurs) as CheckBox
        mFri = v.findViewById<View>(R.id.edit_alarm_fri) as CheckBox
        mSat = v.findViewById<View>(R.id.edit_alarm_sat) as CheckBox
        mSun = v.findViewById<View>(R.id.edit_alarm_sun) as CheckBox
        setDayCheckboxes(alarm)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_alarm_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> save()
            R.id.action_delete -> delete()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAlarm(): Alarm {
        return arguments?.getParcelable(EditAlarmActivity.ALARM_EXTRA)!!
    }

    private fun setDayCheckboxes(alarm: Alarm) {
        mMon.isChecked = alarm.getDay(Alarm.MON)
        mTues.isChecked = alarm.getDay(Alarm.TUES)
        mWed.isChecked = alarm.getDay(Alarm.WED)
        mThurs.isChecked = alarm.getDay(Alarm.THURS)
        mFri.isChecked = alarm.getDay(Alarm.FRI)
        mSat.isChecked = alarm.getDay(Alarm.SAT)
        mSun.isChecked = alarm.getDay(Alarm.SUN)
    }

    private fun save() {
        val alarm = getAlarm()
        val time = Calendar.getInstance()
        time[Calendar.MINUTE] = getTimePickerMinute(mTimePicker)
        time[Calendar.HOUR_OF_DAY] = getTimePickerHour(mTimePicker)
        alarm.time = time.timeInMillis
        alarm.label = mLabel.text.toString()
        alarm.setDay(Alarm.MON, mMon.isChecked)
        alarm.setDay(Alarm.TUES, mTues.isChecked)
        alarm.setDay(Alarm.WED, mWed.isChecked)
        alarm.setDay(Alarm.THURS, mThurs.isChecked)
        alarm.setDay(Alarm.FRI, mFri.isChecked)
        alarm.setDay(Alarm.SAT, mSat.isChecked)
        alarm.setDay(Alarm.SUN, mSun.isChecked)
        val rowsUpdated: Int = DatabaseHelper.getInstance(context).updateAlarm(alarm)
        val messageId: Int =
            if (rowsUpdated == 1) R.string.update_complete else R.string.update_failed
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
        AlarmReceiver.setReminderAlarm(requireContext(), alarm)
        requireActivity().finish()
    }

    private fun delete() {
        val alarm = getAlarm()
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.DeleteAlarmDialogTheme)
        builder.setTitle(R.string.delete_dialog_title)
        builder.setMessage(R.string.delete_dialog_content)
        builder.setPositiveButton(R.string.yes
        ) { _, _ ->
            AlarmReceiver.cancelReminderAlarm(requireContext(), alarm)
            val rowsDeleted: Int = DatabaseHelper.getInstance(context).deleteAlarm(alarm)
            val messageId: Int
            if (rowsDeleted == 1) {
                messageId = R.string.delete_complete
                Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
                LoadAlarmsService.enqueueWork(requireContext())
                requireActivity().finish()
            } else {
                messageId = R.string.delete_failed
                Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(R.string.no, null)
        builder.show()
    }
}