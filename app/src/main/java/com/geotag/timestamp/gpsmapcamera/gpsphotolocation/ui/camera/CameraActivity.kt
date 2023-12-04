package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityCameraBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.setColorResource
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.HomeViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.MediaActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.stamp.StampOptionAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class CameraActivity @Inject constructor() : AbsActivity<ActivityCameraBinding>(),
    OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private val handler = Handler()
    private lateinit var mediaPlayer: MediaPlayer

    //video
    private var recording: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    // Photo
    private var imageCapture: ImageCapture? = null

    private var startTimeMillis: Long = 0
    private var timer: Timer? = null
    private var isSwap = false
    private var isPlayVideo = false
    private var isChangeText = false

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var mapFragment: SupportMapFragment? = null

    private var locationFile = MutableLiveData<Location>()
    private var locationCurrent: Location? = null

    private val viewModel by viewModels<HomeViewModel>()


    override fun initView() {
        requestPermissions()
        startCamera()
        setUpMap()
        mediaPlayer = MediaPlayer.create(this, R.raw.camera)
        val file = sharePrefs.cameraRecent?.let { File(it) }
        file?.let { readFileImage(it) }
        updateDateTime()
        initLogo()
        initViewPager()
    }

    private fun initLogo(){
        sharePrefs.logoSelect.takeIf { it?.isNotEmpty()==true }?.let {
            Glide.with(this)
                .load(it)
                .transform(RoundedCorners(binding.layoutLogo.radius.toInt()))
                .centerCrop()
                .into(binding.imgLogo)
        }?: kotlin.run {
            binding.imgLogo.setImageResource(R.drawable.logo_default)
        }
    }

    private fun initViewPager() {
        binding.apply {
            if (viewPager.adapter == null) {
                viewPager.isUserInputEnabled = true
                viewPager.isSaveEnabled = false
                viewPager.offscreenPageLimit = 2
                viewPager.adapter = object : FragmentStateAdapter(this@CameraActivity) {

                    override fun getItemCount(): Int {
                        return 2
                    }

                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> Fragment()
                            1 -> Fragment()
                            else -> throw IllegalStateException(
                                "ViewPage position $position"
                            )
                        }
                    }
                }
                TabLayoutMediator(
                    tabLayout,
                    viewPager
                ) { tab, position ->
                    tab.text = when (position) {
                        0 -> getString(R.string.camera_photo)
                        1 -> getString(R.string.camera_video)
                        else -> ""
                    }
                }.attach()

                intent?.apply {
                    viewPager.currentItem = this.getIntExtra(TYPE_CAMERA, 0)
                    bindTypeCamera(viewPager.currentItem)
                }

                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        bindTypeCamera(position)
                    }
                })
            }
        }
    }

    private fun bindTypeCamera(position:Int){
        if (position == 0) {
            binding.ivCamera.setImageResource(R.drawable.button_camera)
            binding.ivCamera.setOnClickListener {
                capturePhoto()
            }
        } else {
            binding.ivCamera.setImageResource(R.drawable.button_video)
            binding.ivCamera.setOnClickListener {
                isPlayVideo = !isPlayVideo
                if (isPlayVideo) {
                    captureVideo()
                    binding.ivCamera.setImageResource(R.drawable.button_stop_video)
                } else {
                    recording?.run {
                        this.stop()
                        recording = null
                    }
                    stopTimer()
                    binding.timeTxt.visibility = View.GONE
                    binding.ivCamera.setImageResource(R.drawable.button_video)
                }
            }
        }
    }

    private fun setUpMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun initAction() {
        binding.swapCamera.setOnClickListener {
            isSwap = !isSwap
            cameraSelector = if (isSwap) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
        binding.ivSelect.setOnClickListener {
            startActivity(StampOptionAct.getIntent(this))
        }
        binding.ivRecent.setOnClickListener {
            val medias=viewModel.getMediaFile(this@CameraActivity)
            medias.map { viewModel.insert(it) }
            if (sharePrefs.cameraRecent?.endsWith(".mp4") == true || sharePrefs.cameraRecent?.endsWith(".3gp") == true) {
                MediaActivity.startActivity(this, Constants.TYPE_VIDEO)
            } else {
                MediaActivity.startActivity(this, Constants.TYPE_IMAGE)
            }
            finish()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_camera
    }

    override fun bindViewModel() {

    }

    // create video
    private fun captureVideo() {
        val name: String =
            SimpleDateFormat("yyyy-  MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(
                System.currentTimeMillis()
            )
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")

        val options = MediaStoreOutputOptions.Builder(
            contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setLocation(locationCurrent).setContentValues(contentValues).build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        recording =
            videoCapture?.output?.prepareRecording(this@CameraActivity, options)?.withAudioEnabled()
                ?.start(ContextCompat.getMainExecutor(this@CameraActivity)) { videoRecordEvent ->
                    if (videoRecordEvent is VideoRecordEvent.Start) {
//                        binding.startBtn.isEnabled = true
                        binding.swapCamera.visibility = View.GONE
                        startTimer()
                    } else if (videoRecordEvent is VideoRecordEvent.Finalize) {
                        if (!videoRecordEvent.hasError()) {
                            stopTimer()
                            Toast.makeText(this, getString(R.string.save_video_success), Toast.LENGTH_SHORT).show()
                            val uri = videoRecordEvent.outputResults.outputUri
                            val documentFile: DocumentFile? = DocumentFile.fromSingleUri(this, uri)
                            val fileVideo = documentFile?.let { convertDocumentFileToFile(this, it) }
                            sharePrefs.cameraRecent = fileVideo?.path
                            fileVideo?.let { readFileImage(it) }
                            binding.swapCamera.visibility = View.VISIBLE
                            val medias=viewModel.getMediaFile(this@CameraActivity)
                            medias.map { viewModel.insert(it) }
                        } else {
                            val msg = "Error: " + videoRecordEvent.error
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                            recording?.close()
                            recording = null
                        }
                    }
                }
    }

    private fun capturePhoto() {
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    mediaPlayer.start()
                    val bitmap = imageProxyToBitmap(image)
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    saveBitmapToFile(bitmap, fileName, locationFile.value)
                    binding.ivRecent.setImageBitmap(bitmap)
                    image.close()
                    val medias=viewModel.getMediaFile(this@CameraActivity)
                    medias.map { viewModel.insert(it) }
                }


                override fun onError(exception: ImageCaptureException) {
                    Log.i("TuanAnh photo", "onError: ")
                }
            }
        )


    }


    private fun startTimer() {
        startTimeMillis = System.currentTimeMillis()
        binding.timeTxt.visibility = View.VISIBLE

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis
                val elapsedTimeInSeconds = elapsedTimeMillis / 1000

                runOnUiThread {
                    // Cập nhật thời gian ghi video ở đây
                    val formattedTime = formatTime(elapsedTimeInSeconds)
                    binding.timeTxt.text = formattedTime
                }
            }
        }, 0, 1000) // Cập nhật mỗi giây (1000 ms)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun requestPermissions() {
        requestCameraPermissionIfMissing {
            if (it) {
                startCamera()
            } else {
                Toast.makeText(this, getString(R.string.please_allow_the_permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestCameraPermissionIfMissing(onResult: ((Boolean) -> Unit)) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Quyền CAMERA đã được cấp, tiếp tục thực hiện các công việc liên quan đến camera
            startCamera()
        } else {
            // Quyền CAMERA chưa được cấp, bạn có thể yêu cầu cấp quyền từ người dùng
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                onResult(it)
            }.launch(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Quyền RECORD_AUDIO đã được cấp, tiếp tục thực hiện các công việc liên quan đến camera
            startCamera()
        } else {
            // Quyền RECORD_AUDIO chưa được cấp, bạn có thể yêu cầu cấp quyền từ người dùng
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                onResult(it)
            }.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startCamera() {
        val processCamProvider = ProcessCameraProvider.getInstance(this)
        processCamProvider.addListener({
            val cameraProvider = processCamProvider.get()
            val previewUseCase = Preview.Builder().build()
            previewUseCase.setSurfaceProvider(binding.cameraView.surfaceProvider)

            videoCapture = VideoCapture.withOutput(
                Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()
            )
            imageCapture = ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewUseCase,
                imageCapture,
                videoCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String, location: Location?) {
        val directory = getOutputDirectory() // Get the directory to save the image
        val file = File(directory, fileName)

        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()

        // Add the saved image to the system's MediaStore for it to be visible in gallery apps

//        MediaStore.Images.Media.insertImage(contentResolver, file.path, fileName, null)
        scanMedia(file.path)
        sharePrefs.cameraRecent = file.path
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                val exif = ExifInterface(file.path)
                exif.setLatLong(locationCurrent?.latitude?:0.0, locationCurrent?.longitude?:0.0)
                exif.setAttribute(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL.toString()
                )
                exif.saveAttributes()
            }, 300)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun readFileImage(imgFile: File) {
        if (imgFile.exists()) {
            Glide.with(this).load(imgFile.path).into(binding.ivRecent)
        }
    }

    private fun convertDocumentFileToFile(context: Context, documentFile: DocumentFile): File? {
        val inputStream = context.contentResolver.openInputStream(documentFile.uri)

        if (inputStream != null) {
            val outputFile = File(context.cacheDir, documentFile.name ?: "converted_file")
            val outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(4 * 1024) // 4K buffer size
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            return outputFile
        }

        return null
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { file ->
            File(file, "Images").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun getIsShowMap(): Boolean {
        return sharePrefs.isShowMap
    }

    override fun onResume() {
        super.onResume()
        binding.layoutMap.isVisible = getIsShowMap()
        handleChangeTheme()
    }

    private fun handleChangeTheme() {
        if (sharePrefs.colorCurrent > 0) {
            binding.txtTime.setColorResource(sharePrefs.colorCurrent)
            binding.txtAddress.setColorResource(sharePrefs.colorCurrent)
            binding.txtLat.setColorResource(sharePrefs.colorCurrent)
            binding.txtLong.setColorResource(sharePrefs.colorCurrent)
            binding.txtAddress.setColorResource(sharePrefs.colorCurrent)
            binding.txtDate.setColorResource(sharePrefs.colorCurrent)
        }
        if (sharePrefs.fontCurrent > 0) {
            binding.txtTime.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.txtAddress.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.txtLat.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.txtLong.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.txtAddress.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.txtDate.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)

        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // disable button current location default
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        if (SystemUtil.haveNetworkConnection(this)) showCurrentLocation(12f) else Toast.makeText(this, getString(R.string.check_permission_internet),
            Toast.LENGTH_SHORT
        ).show()

    }

    @SuppressLint("MissingPermission")
    private fun showCurrentLocation(zoom: Float) {
        googleMap.isMyLocationEnabled = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    locationCurrent = it
                    locationFile.postValue(location)
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    DeviceUtil.getAddress(location, this, binding.txtAddress)
                    binding.txtLat.text = String.format("%.2f", location.latitude)
                    binding.txtLong.text = String.format("%.2f", location.longitude)
                    updateDateTime()
                    val delayMillis = 1000L // Update every 1 second
                    handler.postDelayed(updateTimeRunnable, delayMillis)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom))
                }
            }
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateDateTime()
            handler.postDelayed(this, 1000) // Schedule the next update
        }
    }

    private fun updateDateTime() {
        val currentDateTime = LocalDateTime.now()
        val time = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formattedTime = currentDateTime.format(time)

        val date = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val formattedDate= currentDateTime.format(date)
        binding.txtDate.text = formattedDate
        binding.txtTime.text = formattedTime
    }


    companion object {
        const val TYPE_CAMERA="CameraActivity_Type"
        fun getIntent(context: Context,type:Int): Intent {
            val intent=Intent(context, CameraActivity::class.java)
            intent.putExtra(TYPE_CAMERA,type)
            return intent
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun scanMedia(path: String) {
        val file = File(path)
        val uri = Uri.fromFile(file)
        val scanFileIntent = Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri
        )
        sendBroadcast(scanFileIntent)
    }
}