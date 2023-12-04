package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.Note
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityDetailBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beGone
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beVisible
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.onClickListener
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.setColorResource
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.slideDownAnimation
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.slideUpAnimation
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.toast
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.dialog.DialogConfirmDelete
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.dialog.DialogNote
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.dialog.ListenerAddNote
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.dialog.ListenerDelete
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil.convertCoordinateToDegreesLat
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil.convertCoordinateToDegreesLong
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil.getIntentSend
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil.getIntentSendVideo
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Strings
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class DetailActivity @Inject constructor() : AbsActivity<ActivityDetailBinding>(),
    OnMapReadyCallback {
    private var mMediaModel: MediaModel? = null
    private var googleMap: GoogleMap? = null

    //    private lateinit var bottomSheetInfo: BottomSheetInfo
    private var isShownBottomSheet = false
    private var fileShare: File? = null

    private var dialogConfirmDelete: DialogConfirmDelete? = null
    private var dialogAddNote: DialogNote? = null

    private var mapFragment: SupportMapFragment? = null

    private var launchDelete =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                toast(R.string.delete_successfully)
                finish()
            }
        }

    private val viewModel by viewModels<DetailViewModel>()

    companion object {
        fun startActivity(activity: Activity, mediaModel: MediaModel) {
            activity.startActivity(Intent(activity, DetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_DATA_TO_DETAIL, mediaModel)
            })
        }
    }


    override fun initView() {
        setUpMap()
        dialogConfirmDelete = DialogConfirmDelete(this)
        dialogAddNote = DialogNote(this)
        if (intent?.extras != null) {
            binding.tvTitle.isSelected = true
            val mediaModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras?.getParcelable(Constants.EXTRA_DATA_TO_DETAIL, MediaModel::class.java)
            } else {
                intent.extras?.getParcelable(Constants.EXTRA_DATA_TO_DETAIL)
            }
            mMediaModel = mediaModel
            binding.tvTitle.text = mediaModel?.name
            binding.tvDateCreate.text = SimpleDateFormat(
                "EEE, d MMM, yyyy",
                Locale.getDefault()
            ).format(mediaModel?.dateTaken)
            binding.tvTimeCreate.text =
                DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                    .format(mediaModel?.dateTaken)
            if (mMediaModel?.type == Constants.TYPE_IMAGE) {
                binding.videoView.beGone()
                Glide.with(binding.imgDetail.context).load(mMediaModel?.data)
                    .into(binding.imgDetail)
            } else {
                binding.imgDetail.beGone()
                binding.videoView.setVideoPath(mMediaModel?.data)
                binding.videoView.start()
                binding.videoView.requestFocus()
            }
            initViewDetail(mediaModel!!)
            initBottomSheet(mediaModel)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViewDetail(mediaModel: MediaModel) {
        viewModel.getNoteByPath(mMediaModel?.data ?: "").observe(this) {
            it.takeIf { it != null }?.let { note ->
                binding.tvNote.text = (getString(R.string.note) + note.textNote)
            }?: kotlin.run {
                binding.tvNote.text=getString(R.string.note)
            }
        }
        if (sharePrefs.logoSelect?.isNotEmpty() == true) {
            Glide.with(this)
                .load(sharePrefs.logoSelect)
                .transform(RoundedCorners(binding.layoutLogo.radius.toInt()))
                .centerCrop()
                .into(binding.imgLogo)
        }
        if (mediaModel.type == Constants.TYPE_IMAGE) {
            setLocationWithImage(mediaModel)
        }
        if (mediaModel.type == Constants.TYPE_VIDEO) {
            setLocationWithVideo(mediaModel)
        }
    }

    private fun setLocationWithVideo(mediaModel: MediaModel) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(mediaModel.data)
            val location = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION)
            if (!location.isNullOrEmpty()) {
                val lat = location.substring(0, location.indexOf("+", 2, false)).replace("+", "")
                val long = location.substring(location.indexOf("+", 2, false), location.length - 2)
                    .replace("+", "")
                val mLat = convertCoordinateToDegreesLat(lat.toDouble())
                val mLong = convertCoordinateToDegreesLong(long.toDouble())
                binding.tvPosition.text = "$mLat    $mLong"
                getLocationFromPosition(lat.toDouble(), long.toDouble(), {
                    binding.tvAddressTake.text = it
                    binding.tvAddressTake.isSelected = true
                }, {
                    binding.tvAddressTake.text = it
                    binding.tvAddressTake.text = it
                })
            } else {
                binding.tvPosition.text =
                    resources.getString(R.string.can_t_find_the_longitude_and_latitude_of_this_photo)
                binding.tvAddressTake.text =
                    resources.getString(R.string.could_not_find_this_image_address)
                binding.tvPosition.isSelected = true
                binding.tvAddressTake.isSelected = true
            }
        } catch (ex: Exception) {
            Log.e("LocationVideo", "Error reading location from video: ${ex.message}")

            ex.printStackTrace()
        } finally {
            retriever.release()
        }
    }

    private fun getLocationFromPosition(
        lat: Double,
        long: Double,
        onSuccess: (s: String) -> Unit,
        onError: (err: String?) -> Unit
    ) {
        val geocoder = Geocoder(this, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, long, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    onSuccess(addresses[0].getAddressLine(0))
                }

                override fun onError(errorMessage: String?) {
                    onError(errorMessage)
                    super.onError(errorMessage)
                }
            })
        } else {
            try {
                val addresses = geocoder.getFromLocation(lat, long, 1)
                if (addresses != null) {
                    onSuccess(addresses[0].getAddressLine(0))
                } else {
                    onError(resources.getString(R.string.could_not_find_this_image_address))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun setLocationWithImage(mediaModel: MediaModel) {
        val exif = ExifInterface(mediaModel.data)
        val latLong = FloatArray(2)
        if (exif.getLatLong(latLong)) {
            val lat = convertCoordinateToDegreesLat(latLong[0].toDouble())
            val long = convertCoordinateToDegreesLong(latLong[1].toDouble())
            binding.tvPosition.text = "$lat    $long"
            getLocationFromPosition(latLong[0].toDouble(), latLong[1].toDouble(), {
                binding.tvAddressTake.text = it
                binding.tvAddressTake.isSelected = true
            }, {
                binding.tvAddressTake.text = it
                binding.tvAddressTake.text = it

            })
        } else {
            binding.tvPosition.text =
                resources.getString(R.string.can_t_find_the_longitude_and_latitude_of_this_photo)
            binding.tvAddressTake.text =
                resources.getString(R.string.could_not_find_this_image_address)
            binding.tvPosition.isSelected = true
            binding.tvAddressTake.isSelected = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initBottomSheet(mediaModel: MediaModel) {
        binding.tvName.text = mediaModel.name
        binding.tvDate.text = DateFormat.getDateTimeInstance(
            DateFormat.DEFAULT,
            DateFormat.SHORT,
            Locale.getDefault()
        ).format(mediaModel.dateTaken)
        binding.tvPath.text = mediaModel.data
        if (mediaModel.type == Constants.TYPE_IMAGE) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(mediaModel.data, options)
            binding.tvResolution.text = "${options.outWidth}x${options.outHeight}"
            binding.viewResolution.beVisible()
        } else {
            val mmr = MediaMetadataRetriever()
            try {
                mmr.setDataSource(mediaModel.data)
                val width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()
                val height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()
                binding.tvResolution.text = "${width}x${height}"
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                mmr.release()
            }

        }
    }

    override fun initAction() {
        binding.btnClose.setOnClickListener {
            if (isShownBottomSheet) {
                isShownBottomSheet = false
                binding.viewBottomSheet.slideDownAnimation(this)
            }
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.viewContainer.setOnClickListener {
            if (isShownBottomSheet) {
                isShownBottomSheet = false
                binding.viewBottomSheet.slideDownAnimation(this)
            }
        }
        binding.btnShare.onClickListener {
            handleShare(it)
        }
        binding.btnEdit.onClickListener {
            handleEdit(it)
        }
        binding.btnInfo.onClickListener {
            handleInfo(it)
        }
        binding.btnDelete.onClickListener {
            handleDelete(it)
        }
    }

    override fun getContentView(): Int = R.layout.activity_detail

    override fun bindViewModel() {

    }

    private fun handleShare(v: View) {
        binding.imgMap.alpha = 1F
        if (binding.imgMap.width > 0 && binding.imgMap.height > 0) {
            googleMap?.snapshot { bitmap ->
                // radius when share
                Glide.with(this)
                    .load(bitmap)
                    .transform(RoundedCorners(binding.layoutMap.radius.toInt()))
                    .into(binding.imgMap)

                Glide.with(this)
                    .load(binding.imgLogo.drawable)
                    .transform(RoundedCorners(binding.layoutLogo.radius.toInt()))
                    .into(binding.imgLogo)
            }
        }
        lifecycleScope.launch {
            delay(500)
            if (mMediaModel != null) {
                val bitmap = fileToBitmap(mMediaModel?.data!!)
                val fileName = "image_${System.currentTimeMillis()}.jpg"
                bitmap?.let { saveBitmapToFile(it, fileName) }
                if (isShownBottomSheet) {
                    isShownBottomSheet = false
                    binding.viewBottomSheet.slideDownAnimation(this@DetailActivity)
                }
                if (mMediaModel?.type == Constants.TYPE_IMAGE) {
                startActivity(Intent.createChooser(getIntentSend(fileShare?.path!!), "Share with"))
                } else {
                startActivity(Intent.createChooser(getIntentSendVideo(mMediaModel?.data!!), "Share with")) }
            }
        }
    }


    fun fileToBitmap(filePath: String): Bitmap? {
        // Decode the file into a Bitmap
        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBitmapToFile(inputBitmap: Bitmap, fileName: String) {
        val directory = getOutputDirectory() // Lấy thư mục để lưu ảnh
        val file = File(directory, fileName)

        val bitmap = inputBitmap.copy(
            inputBitmap.config,
            true
        ) // Tạo bản sao có thể thay đổi của bitma p đầu vào
        val bitmap2 = viewToBitmap(binding.viewShowLocation)
        val combinedBitmap = combineBitmapsOnRightBottom(bitmap, bitmap2, 50)

        val stream = FileOutputStream(file)
        combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()

        // Thêm ảnh đã lưu vào MediaStore của hệ thống để nó hiển thị trong các ứng dụng xem ảnh
        sharePrefs.cameraRecent = file.path
        fileShare = file
        sharePrefs.fileShare = file.path
        scanMedia(file.path)

//        val contentResolver = applicationContext.contentResolver
//        MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, fileName, null)
    }

    private fun combineBitmapsOnRightBottom(
        viewBitmap: Bitmap,
        bitmap2: Bitmap,
        margin: Int
    ): Bitmap {
        val combinedBitmap = Bitmap.createBitmap(
            viewBitmap.width,
            viewBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(combinedBitmap)

        // Vẽ viewBitmap lên canvas
        canvas.drawBitmap(viewBitmap, 0f, 0f, null)

        // Tính toán vị trí để vẽ bitmap2 xuống góc dưới bên phải với margin
        val x = viewBitmap.width - bitmap2.width - margin.toFloat()
        val y = viewBitmap.height - bitmap2.height - margin.toFloat()

        // Vẽ bitmap2 lên canvas ở vị trí mới tính được
        canvas.drawBitmap(bitmap2, x, y, null)

        return combinedBitmap
    }

    private fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { file ->
            File(file, "Images").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    private fun handleInfo(v: View) {
        if (!isShownBottomSheet) {
            isShownBottomSheet = true
            binding.viewBottomSheet.slideUpAnimation(this)
        } else {
            isShownBottomSheet = false
            binding.viewBottomSheet.slideDownAnimation(this)
        }
    }

    private fun handleEdit(v: View) {
        dialogAddNote?.show()
        val parts = binding.tvNote.text.toString().split(":")
        if (parts.size > 1) {
            val result = parts[1].trim() // Lấy phần sau dấu ":" và loại bỏ khoảng trắng
            dialogAddNote?.setContent(result)

        }
        dialogAddNote?.setListener(object : ListenerAddNote {
            override fun confirm(text: String) {
                val note = Note(null, mMediaModel?.data ?: "", text)
                viewModel.insert(note)
            }
        })
    }

    private fun handleDelete(v: View) {
        dialogConfirmDelete?.show()
        if(mMediaModel?.type==0){
            dialogConfirmDelete?.setType(getString(R.string.camera_photo))
        }else{
            dialogConfirmDelete?.setType(getString(R.string.camera_video))
        }
        dialogConfirmDelete?.setListener(object : ListenerDelete {
            override fun confirm() {
                if (v.isEnabled) {
                    if (mMediaModel?.data != null) {
                        val file = File(mMediaModel?.data!!)
                        DeviceUtil.handleDeleteFile(this@DetailActivity, file, this@DetailActivity,launchDelete)
                        mMediaModel.takeIf { it != null }?.let { viewModel.delete(it) }
                    }
                    v.isEnabled = false
                    v.postDelayed({ v.isEnabled = true }, 600)
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if (fileShare?.exists() == true) {
            fileShare?.delete()
            sharePrefs.fileShare = ""
        }
        binding.layoutMap.isVisible = sharePrefs.isShowMap
        binding.imgMap.alpha = 0F
//        sharePrefs.fileShare?.let { File(it).delete() }
        handleChangeByTheme()
    }

    private fun handleChangeByTheme() {
        if (sharePrefs.colorCurrent > 0) {
            binding.tvDateCreate.setColorResource(sharePrefs.colorCurrent)
            binding.tvTimeCreate.setColorResource(sharePrefs.colorCurrent)
            binding.tvPosition.setColorResource(sharePrefs.colorCurrent)
            binding.tvAddressTake.setColorResource(sharePrefs.colorCurrent)
            binding.tvNote.setColorResource(sharePrefs.colorCurrent)
        }
        if (sharePrefs.fontCurrent > 0) {
            binding.tvDateCreate.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.tvTimeCreate.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.tvPosition.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.tvAddressTake.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.tvNote.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
        }
    }

    private fun scanMedia(path: String) {
        val file = File(path)
        val uri = Uri.fromFile(file)
        val scanFileIntent = Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri
        )
        sendBroadcast(scanFileIntent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        super.onWindowFocusChanged(hasFocus)
    }

    private fun setUpMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // disable button current location default
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false

        if (mMediaModel?.type == Constants.TYPE_IMAGE) {
            val exif = ExifInterface(mMediaModel?.data ?: "")
            val latLong = FloatArray(2)
            if (exif.getLatLong(latLong)) {
                if (SystemUtil.haveNetworkConnection(this)) showLocationImage(
                    12f,
                    latLong[0].toDouble(),
                    latLong[1].toDouble()
                )
                else Toast.makeText(this,
                    getString(R.string.you_need_to_turn_on_the_internet), Toast.LENGTH_SHORT).show()
            } else {
                if (SystemUtil.haveNetworkConnection(this)) showCurrentLocation(12f)
                else Toast.makeText(this, getString(R.string.you_need_to_turn_on_the_internet), Toast.LENGTH_SHORT).show()

            }
        }
        if (mMediaModel?.type == Constants.TYPE_VIDEO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(mMediaModel?.data ?: "")
                val location =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION)
                if (!location.isNullOrEmpty()) {
                    val lat =
                        location.substring(0, location.indexOf("+", 2, false)).replace("+", "")
                    val long =
                        location.substring(location.indexOf("+", 2, false), location.length - 2)
                            .replace("+", "")
                    if (SystemUtil.haveNetworkConnection(this)) showLocationImage(
                        12f,
                        lat.toDouble(),
                        long.toDouble()
                    )
                    else Toast.makeText(this, getString(R.string.you_need_to_turn_on_the_internet), Toast.LENGTH_SHORT).show()
                } else {
                    if (SystemUtil.haveNetworkConnection(this)) showCurrentLocation(12f)
                    else Toast.makeText(this, getString(R.string.you_need_to_turn_on_the_internet), Toast.LENGTH_SHORT).show()
                }
            } catch (ex: Exception) {
                Log.e("LocationVideo", "Error reading location from video: ${ex.message}")
                ex.printStackTrace()
            } finally {
                retriever.release()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showLocationImage(zoom: Float, lat: Double, long: Double) {
        googleMap?.isMyLocationEnabled = true
        val currentLatLng = LatLng(lat, long)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom))
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentLocation(zoom: Float) {
        googleMap?.isMyLocationEnabled = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom))
                }
            }
    }
}