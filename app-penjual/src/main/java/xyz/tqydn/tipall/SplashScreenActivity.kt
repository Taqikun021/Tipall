package xyz.tqydn.tipall

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import xyz.tqydn.tipall.databinding.ActivitySplashScreenBinding
import xyz.tqydn.tipall.ui.LoginActivity
import xyz.tqydn.tipall.utils.SharedPreference

@SuppressLint("MissingPermission")
class SplashScreenActivity: AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var binding: ActivitySplashScreenBinding
    private val permissionID = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("Debug : ", checkPermission().toString())
        Log.d("Debug : ", isLocationEnabled().toString())
        reqPermission()
        getLastLocation()
        binding.loading.visibility = View.VISIBLE
        if (checkPermission()){
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                binding.loading.visibility = View.GONE
                startActivity(intent)
                finish()
            }, 1500)
        }
    }

    private fun getLastLocation() {
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {task->
                    val location : Location? = task.result
                    if(location == null){
                        newLocationData()
                    } else {
                        preference.setValues("lat", location.latitude.toString())
                        preference.setValues("long", location.longitude.toString())
                    }
                }
            } else {
                Toast.makeText(this,"Please Turn on Your device Location", Toast.LENGTH_SHORT).show()
            }
        } else {
            reqPermission()
        }
    }

    private fun newLocationData() {
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            Toast.makeText(this@SplashScreenActivity, lastLocation.latitude.toString() + " , " +lastLocation.longitude.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun reqPermission() {
        ActivityCompat.requestPermissions(this, arrayOf
            (android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
            permissionID
        )
    }

    private fun checkPermission(): Boolean {
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun isLocationEnabled():Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == permissionID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the Permission")
            }
        }
    }
}