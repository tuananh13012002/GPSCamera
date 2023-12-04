package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.format.DateFormat
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.toast
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

object DeviceUtil {


    fun hasStoragePermission(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        else
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun checkTime(inputPath: String): String {
        val lastModifiedTime = getFileLastModifiedTime(inputPath)
        val formattedTime = DateFormat.format("yyyy/MM/dd hh:mm a", lastModifiedTime)
        return formattedTime.toString()
    }

    private fun getFileLastModifiedTime(filePath: String): Long {
        val file = File(filePath)
        return file.lastModified()
    }

    fun formatFileSize(fileSize: Long): String {
        val unit = 1024
        if (fileSize < unit) return "$fileSize B"
        val exp = (ln(fileSize.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1]
        return String.format("%.1f %sB", fileSize / unit.toDouble().pow(exp.toDouble()), pre)
    }

    fun convertCoordinateToDegreesLat(latitude: Double): String {
        val degreeSymbol = "\u00B0"
        val minuteSymbol = "\u2032"
        val secondSymbol = "\u2033"

        val degrees = Math.floor(latitude).toInt()
        val minutes = Math.floor((latitude - degrees) * 60).toInt()
        val seconds = Math.round(((latitude - degrees) * 60 - minutes) * 60).toInt()

        val direction = if (degrees >= 0) "N" else "S"

        return "${Math.abs(degrees)}$degreeSymbol${Math.abs(minutes)}$minuteSymbol${Math.abs(seconds)}$secondSymbol $direction"
    }

    fun convertCoordinateToDegreesLong(longitude: Double): String {
        val degreeSymbol = "\u00B0"
        val minuteSymbol = "\u2032"
        val secondSymbol = "\u2033"

        val degrees = floor(abs(longitude)).toInt()
        val minutes = floor((abs(longitude) - degrees) * 60).toInt()
        val seconds = (((abs(longitude) - degrees) * 60 - minutes) * 60).roundToInt()

        val direction = if (longitude >= 0) "E" else "W"

        return "${abs(degrees)}$degreeSymbol${abs(minutes)}$minuteSymbol${abs(seconds)}$secondSymbol $direction"
    }

    fun checkDegreeDirection(degree: Int): String {
        val direction = when (degree) {
            in 0..44 -> "N"
            in 45..89 -> "NE"
            in 90..134 -> "E"
            in 135..179 -> "SE"
            in 180..224 -> "S"
            in 225..269 -> "SW"
            in 270..314 -> "W"
            in 315..359 -> "NW"
            else -> "Invalid Degree"
        }
        return ("$degree° $direction")
    }

    val arrayStoragePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        else
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

    val arrayRecordPermission = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )
    val arrayLocationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    val arrayBodySensorsPermission = arrayOf(
        Manifest.permission.BODY_SENSORS,
//        Manifest.permission.HIGH_SAMPLING_RATE_SENSORS,
    )

    fun Context.getIntentSend(uri: String): Intent {
        val fileUri = FileProvider.getUriForFile(this, "${packageName}.provider", File(uri))
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun Context.getIntentSendVideo(uri: String): Intent {
        val fileUri = FileProvider.getUriForFile(this, "${packageName}.provider", File(uri))
        return Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun convertToDecimal(input: String?): Double {
        if (input == null) return 0.0

        val parts = input.split(",")

        if (parts.size != 3) return 0.0

        val degrees = parts[0].toDouble()
        val minutes = parts[1].toDouble()
        val seconds = parts[2].toDouble()

        return degrees + minutes / 60 + seconds / 3600
    }

    @Suppress("DEPRECATION")
    fun getAddress(location: Location, context: Context, txtAddress: TextView) {
        val geocoder = Geocoder(context, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        txtAddress.text = addresses[0].getAddressLine(0)
                    }

                    override fun onError(errorMessage: String?) {
                        onError(errorMessage)
                        super.onError(errorMessage)
                    }
                })
        } else {
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null) {
                    txtAddress.text = addresses[0].getAddressLine(0)
                } else {
                    txtAddress.text = (context.getString(R.string.could_not_find_this_image_address))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @Suppress("DEPRECATION")
    val locationRequest = LocationRequest.create().apply {
        interval = 3000 // 5 seconds
        fastestInterval = 3000 // 5 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    fun handleDeleteFile(
        context: Context, file: File,
        activity: Activity,
        launchDelete: ActivityResultLauncher<IntentSenderRequest>
    ) {
        if (file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf<String>(file.absolutePath),
                    null
                ) { _, uri: Uri ->
                    val pendingIntent =
                        MediaStore.createDeleteRequest(context.contentResolver, arrayListOf(uri))
                    val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
                    launchDelete.launch(intentSenderRequest)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf<String>(file.absolutePath),
                    null
                ) { _, uri: Uri ->
                    context.contentResolver.delete(uri, null, null)
                    activity.finish()
                }
                scanMedia(context, file.path)
                Handler(Looper.getMainLooper()).postDelayed({
                    context.toast(R.string.delete_successfully)
                    activity.finish()
                }, 600)
            } else {
                file.delete()
                activity.finish()
                context.toast(R.string.delete_successfully)
            }
        } else {
            context.toast(R.string.something_wrong)
        }
    }

    private fun scanMedia(context: Context, path: String) {
        val file = File(path)
        val uri = Uri.fromFile(file)
        val scanFileIntent = Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri
        )
        context.sendBroadcast(scanFileIntent)
    }

}