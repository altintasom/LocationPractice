package com.test.locationpraactice

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


abstract class RuntimePermission : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initViews()
    }

    fun customRequestPermissions(permissions: Array<String?>, requestCode: Int) {
        var checkPermission = PackageManager.PERMISSION_GRANTED
        var reasonShow = false


        //izinkontrol=0 ise izin/izinler verilmiştir
        //aksi durumda izin/izinler verilmemiştir.
        //mazeret göster = false ise ilk defa izin sorulmustur
        //mazeret goster= true ise kullanıcı izni reddetmiştir, ona bir mazeret sunabiliriz.
        for (permission in permissions) {
            checkPermission = checkPermission + ContextCompat.checkSelfPermission(this, permission!!)
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
                            this@RuntimePermission,
                            permissions,
                            requestCode
                        )
                    })
                builder.show()
            } else {
                ActivityCompat.requestPermissions(
                    this@RuntimePermission,
                    permissions,
                    requestCode
                )
            }
        } else {
            permissionsGranted(requestCode)
        }
    }

    abstract fun permissionsGranted(requestCode: Int)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var izinKontrol = PackageManager.PERMISSION_GRANTED


        //izinkontrol=0 ise tüm izinler verilmiştir
        for (izinDurumu in grantResults) {
            izinKontrol = izinKontrol + izinDurumu
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

    abstract fun initViews()
}