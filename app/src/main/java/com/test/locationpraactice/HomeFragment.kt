package com.test.locationpraactice

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.test.locationpraactice.databinding.FragmentHomeBinding
import com.test.locationpraactice.viewmodel.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import java.lang.ClassCastException

private const val TAG = "HomeFragment"
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding : FragmentHomeBinding
    private val viewModel : HomeFragmentViewModel by viewModels()
    private lateinit var locationRequest : LocationRequest
    private lateinit var builder: LocationSettingsRequest.Builder
    companion object{
        const val REQUEST_CHECK_CODE = 8989
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        locationRequest = LocationRequest.create().also {
            it.interval = 10000
            it.fastestInterval = 3000
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)


        openLocation()
        customRequestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),100)
        binding.btnLocation.setOnClickListener {
            openLocation()
            requestLocation()

        }
        binding.lifecycleOwner = this
    }

    /**
     * custom request permission func for location permissions
     * */
    fun customRequestPermissions(permissions: Array<String?>, requestCode: Int) {
        var checkPermission = PackageManager.PERMISSION_GRANTED
        var reasonShow = false


        //izinkontrol=0 ise izin/izinler verilmiştir
        //aksi durumda izin/izinler verilmemiştir.
        //mazeret göster = false ise ilk defa izin sorulmustur
        //mazeret goster= true ise kullanıcı izni reddetmiştir, ona bir mazeret sunabiliriz.
        for (permission in permissions) {
            checkPermission += ContextCompat.checkSelfPermission(requireContext(), permission!!)
            reasonShow = reasonShow || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                permission
            )
        }
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            if (reasonShow) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Neden İzin Vermelisin?")
                builder.setMessage("Arama yapmak istiyorsan bu izni vermen gerekiyor")
                builder.setNegativeButton("IZIN YOK",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                builder.setPositiveButton("IZIN VERMEK ISTIYORUM",
                    DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            permissions,
                            requestCode
                        )
                    })
                builder.show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
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
            Log.d(TAG, "permissionsGranted: ")
        } else {
            Toast.makeText(requireContext(), "İzin verilmedi!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var checkPermission = PackageManager.PERMISSION_GRANTED


        //izinkontrol=0 ise tüm izinler verilmiştir
        for (statePermission in grantResults) {
            checkPermission += statePermission
        }
        if (grantResults.isNotEmpty() && checkPermission == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted(requestCode)
        } else {
            val builder = AlertDialog.Builder(requireContext())
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
                intent.data = Uri.parse("package:${requireActivity().packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(intent)
            }
            builder.show()
        }
    }

    /**
     *
     * request phone location
     *
    * */
    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        binding.setVisible = true

        LocationServices.getFusedLocationProviderClient(requireActivity())
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                @SuppressLint("SetTextI18n")
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        var latestLocationIndex = locationResult.locations.size - 1
                        var latitude = locationResult.locations[latestLocationIndex].latitude
                        var longitude = locationResult.locations[latestLocationIndex].longitude
                        binding.setVisible = false
                        binding.location = "Latitude: $latitude\nLongitude: $longitude"
                    }
                }
            }, Looper.getMainLooper())


    }

    /**
     * open phone location services
     * */
    private fun openLocation(){
        val result : Task<LocationSettingsResponse> = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener(object : OnCompleteListener<LocationSettingsResponse>{
            override fun onComplete(task: Task<LocationSettingsResponse>) {
                try {
                    task.getResult(ApiException::class.java)
                }catch (e : ApiException){
                    when(e.statusCode){
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val resolvableApiException = e as ResolvableApiException
                                resolvableApiException.startResolutionForResult(
                                    requireActivity(),
                                    REQUEST_CHECK_CODE
                                )
                            } catch (ex: IntentSender.SendIntentException) {

                            } catch (ex: ClassCastException) {

                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                        }

                    }
                }
            }

        })
    }

}