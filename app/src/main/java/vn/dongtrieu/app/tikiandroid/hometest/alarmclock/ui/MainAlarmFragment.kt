package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import vn.dongtrieu.app.tikiandroid.hometest.R
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.adapter.AlarmAdapter
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.checkAlarmPermissions
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.model.Alarm
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service.LoadAlarmReceiver
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.service.LoadAlarmsService
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui.EditAlarmActivity.Companion.ADD_ALARM
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui.EditAlarmActivity.Companion.buildAddEditAlarmActivityIntent
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.view.DividerItemDecoration
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.view.EmptyRecyclerView
import vn.dongtrieu.app.tikiandroid.hometest.bind

class MainAlarmFragment: Fragment(), LoadAlarmReceiver.OnAlarmsLoadedListener {

    private val mReceiver = LoadAlarmReceiver(this)
    private val mAdapter = AlarmAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.alarm_main_fragment, container, false)
        val rv: EmptyRecyclerView = v.findViewById(R.id.recycler)
        rv.setEmptyView(v.findViewById(R.id.empty_view))
        rv.adapter = mAdapter
        rv.addItemDecoration(DividerItemDecoration(context))
        rv.layoutManager = LinearLayoutManager(context)
        rv.itemAnimator = DefaultItemAnimator()

        val fab: FloatingActionButton = v.findViewById(R.id.fab)
        fab.setOnClickListener {
            activity?.let {
                checkAlarmPermissions(it)
                val i: Intent = buildAddEditAlarmActivityIntent(it, ADD_ALARM)
                startActivity(i)
            }
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(LoadAlarmsService.ACTION_COMPLETE)
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(mReceiver, filter)
            LoadAlarmsService.enqueueWork(it)
        }

    }

    override fun onAlarmsLoaded(alarms: ArrayList<Alarm>) {
        mAdapter.setAlarm(alarms)
    }
}