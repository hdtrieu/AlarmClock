package vn.dongtrieu.app.tikiandroid.hometest.gps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.dongtrieu.app.tikiandroid.hometest.R
import vn.dongtrieu.app.tikiandroid.hometest.bind
import vn.dongtrieu.app.tikiandroid.hometest.dpF
import kotlin.math.roundToInt
import kotlin.math.sin


class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var mLocationManager: LocationManager
    private var mHasGps = false
    private var mHasNetWork = false
    private var mLocationGPS: Location? = null
    private var mLocationNetWork: Location? = null

    private var mPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    private val textViewResult: TextView by bind(R.id.textViewResult)
    private val circle: TextView by bind(R.id.circle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCheckPermission()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mHasGps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        mHasNetWork = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (mHasGps || mHasNetWork) {
            if (mHasGps) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this)

                val localGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null) {
                    mLocationGPS = localGpsLocation
                }
            }

            if (mHasNetWork) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this)

                val localNetWorkLocation = mLocationManager.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER)
                if (localNetWorkLocation != null) {
                    mLocationNetWork = localNetWorkLocation
                }
            }

            if (mLocationGPS != null && mLocationNetWork != null) {
                if (mLocationGPS!!.accuracy > mLocationNetWork!!.accuracy) {
                    funcDraw(mLocationNetWork!!.accuracy)
                    textViewResult.append("\nLatitude: " + mLocationNetWork!!.latitude)
                    textViewResult.append("\nLongitude: " + mLocationNetWork!!.longitude)
                    textViewResult.append("\nAccuracy: " + mLocationNetWork!!.accuracy)
                } else {
                    funcDraw(mLocationGPS!!.accuracy)
                    textViewResult.append("\nLatitude: " + mLocationGPS!!.latitude)
                    textViewResult.append("\nLongitude: " + mLocationGPS!!.longitude)
                    textViewResult.append("\nAccuracy: " + mLocationGPS!!.accuracy)

                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                        permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                enableView()
        }
    }

    private fun initCheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(mPermissions)) {
                enableView()
            } else {
                requestPermissions(mPermissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }
    }

    private fun enableView() {
        getLocation()
    }

    private fun checkPermission(mPermissions: Array<String>): Boolean {
        var allSuccess = true
        for (i in mPermissions.indices) {
            if (checkCallingOrSelfPermission(mPermissions[i]) == PackageManager.PERMISSION_DENIED) {
                allSuccess = false
            }
        }
        return allSuccess
    }

    private fun funcDraw(radius: Float) {
        val size = radius.roundToInt()*10 + 20
        val bitmap = Bitmap.createBitmap(size*2, size*2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.color = Color.parseColor("#FFFFFF")
        paint.strokeWidth = 3F
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.isDither = true

        val x_end = (size - (radius).roundToInt() *10 * sin(Math.PI / 6) ).toFloat()
        val y_end = (size + (radius).roundToInt() *10 * sin(Math.PI / 3) ).toFloat()
        canvas.drawCircle(size.toFloat() , size.toFloat(), radius*10, paint)
        canvas.drawLine(size.toFloat(), size.toFloat(),x_end, y_end, paint)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(size.toFloat() , size.toFloat(), 3.dpF, paint)
        circle.background = BitmapDrawable(resources, bitmap)
    }

    companion object {
        private const val PERMISSION_REQUEST = 10
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 0F
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 5 * 1
    }

    override fun onLocationChanged(location: Location) {
        funcDraw(location.accuracy)
        if (mHasGps) {
            Log.d("GET_GPS_LOCATION", "hasGPS ${location.accuracy}")
            mLocationGPS = location
            textViewResult.text = ""
            textViewResult.append("\nLatitude: " + mLocationGPS!!.latitude)
            textViewResult.append("\nLongitude: " + mLocationGPS!!.longitude)
            textViewResult.append("\nAccuracy: " + location.accuracy)
        }
        if (mHasNetWork) {
            Log.d("GET_LOCATION", "hasNetWork ${location.accuracy}")
            mLocationNetWork = location
            textViewResult.text = ""
            textViewResult.append("\nLatitude: " + mLocationNetWork!!.latitude)
            textViewResult.append("\nLongitude: " + mLocationNetWork!!.longitude)
            textViewResult.append("\nAccuracy: " + location.accuracy)
        }
    }

}