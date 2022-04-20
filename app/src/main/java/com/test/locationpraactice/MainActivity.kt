package com.test.locationpraactice


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = (supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment).navController
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

/*    fun customRequestPermissions(permissions: Array<String?>, requestCode: Int) {
        var checkPermission = PackageManager.PERMISSION_GRANTED
        var reasonShow = false


        //izinkontrol=0 ise izin/izinler verilmiştir
        //aksi durumda izin/izinler verilmemiştir.
        //mazeret göster = false ise ilk defa izin sorulmustur
        //mazeret goster= true ise kullanıcı izni reddetmiştir, ona bir mazeret sunabiliriz.
        for (permission in permissions) {
            checkPermission =
                checkPermission + ContextCompat.checkSelfPermission(this, permission!!)
            reasonShow = reasonShow || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
            )
        }
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            if (reasonShow) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Neden İzin Vermelisin?")
                builder.setMessage("Arama yapmak istiyorsan bu izni vermen gerekiyor")
                builder.setNegativeButton("IZIN YOK",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                builder.setPositiveButton("IZIN VERMEK ISTIYORUM",
                    DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            permissions,
                            requestCode
                        )
                    })
                builder.show()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permissions,
                    requestCode
                )
            }
        } else {
            permissionsGranted(requestCode)
        }
    }

    fun permissionsGranted(requestCode: Int) {
        if (requestCode == 100) {
            requestLocation()
        } else {
            Toast.makeText(this, "İzin verilmedi!", Toast.LENGTH_LONG).show()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var izinKontrol = PackageManager.PERMISSION_GRANTED


        //izinkontrol=0 ise tüm izinler verilmiştir
        for (statePermission in grantResults) {
            izinKontrol = izinKontrol + statePermission
        }
        if (grantResults.size > 0 && izinKontrol == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted(requestCode)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("IZIN LAZIM")
            builder.setMessage("Ayarladan tüm izinleri vermen gerekiyor")
            builder.setNegativeButton(
                "OLMAZ"
            ) { dialog, which -> dialog.cancel() }
            builder.setPositiveButton(
                "IZIN VERICEM"
            ) { dialog, which ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(intent)
            }
            builder.show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {

        val locationRequest = LocationRequest.create().also {
            it.interval = 10000
            it.fastestInterval = 3000
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                @SuppressLint("SetTextI18n")
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(this@MainActivity)
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        var latestLocationIndex = locationResult.locations.size - 1
                        var latitude = locationResult.locations[latestLocationIndex].latitude
                        var longitude = locationResult.locations[latestLocationIndex].longitude
                    }
                }
            }, Looper.getMainLooper())


    }*/
}


