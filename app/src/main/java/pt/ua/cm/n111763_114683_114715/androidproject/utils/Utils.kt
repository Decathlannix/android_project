package pt.ua.cm.n111763_114683_114715.androidproject.utils

import android.Manifest

object Utils {
    val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    val REQUEST_CODE_CAMERA_PERMISSIONS = 10
    val CAMERA_REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val LOCATION_REQUIRE_PERMISSIONS = arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
}