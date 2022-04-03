package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ui

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import vn.dongtrieu.app.tikiandroid.hometest.R

class LandingPageFragment: Fragment(), SensorEventListener {

    private var lastTime: Long = 0
    private var speed = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var x = 0f
    private var y = 0f
    private var z = 0f
    private var count = 0

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerormeterSensor: Sensor
    private var pm: PowerManager? = null
    private val wl: WakeLock? = null
    private lateinit var vibrator: Vibrator

    companion object {
        const val SHAKE_THRESHOLD = 100
        var DATA_X = SensorManager.DATA_X
        var DATA_Y = SensorManager.DATA_Y
        var DATA_Z = SensorManager.DATA_Z
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_alarm_landing_page, container, false)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val launchMainActivityBtn = v.findViewById<View>(R.id.load_main_activity_btn) as Button
        val dismiss = v.findViewById<View>(R.id.dismiss_btn) as Button
        launchMainActivityBtn.setOnClickListener {
            startActivity(Intent(context, ClockActivity::class.java))
            vibrator.cancel()
            requireActivity().finish()
        }
        dismiss.setOnClickListener {
            vibrator.cancel()
            requireActivity().finish()
        }
        pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
        startvibe()
        return v
    }

    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(
            this,
            accelerormeterSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            val gabOfTime: Long = currentTime - lastTime
            if (gabOfTime > 100) {
                lastTime = currentTime
                x = event.values[SensorManager.DATA_X]
                y = event.values[SensorManager.DATA_Y]
                z = event.values[SensorManager.DATA_Z]
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000
                if (speed > LandingPageFragment.SHAKE_THRESHOLD) {
                    lastTime = currentTime
                    count++ //for shake test
                    if (count == 10) {
                        vibrator.cancel()
                        requireActivity().finish()
                    }
                }
                lastX =
                    event.values[DATA_X]
                lastY =
                    event.values[DATA_Y]
                lastZ =
                    event.values[DATA_Z]
            }
        }
    }

    private fun startvibe() {
        vibrator.vibrate(longArrayOf(100, 1000, 100, 500, 100, 1000), 0)
    }
}