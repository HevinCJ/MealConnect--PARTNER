package com.example.mealconnect.utils

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.vmadalin.easypermissions.EasyPermissions

object Permissions{

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPermissionsGranted(context: Context)=
        EasyPermissions.hasPermissions(context,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.ACCESS_FINE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissions(fragment: Fragment) {
        EasyPermissions.requestPermissions(fragment,"This Application Need Some Permissions To Work Properly",Constants.REQUEST_LOCATION_CODE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    fun isLocationPermissionEnabled(context: Context)=
        EasyPermissions.hasPermissions(context,Manifest.permission.ACCESS_FINE_LOCATION)


    fun requestLocationPermissions(fragment: Fragment) {
        EasyPermissions.requestPermissions(fragment,"This Application Need Some Permissions To Work Properly",Constants.REQUEST_LOCATION_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }






}