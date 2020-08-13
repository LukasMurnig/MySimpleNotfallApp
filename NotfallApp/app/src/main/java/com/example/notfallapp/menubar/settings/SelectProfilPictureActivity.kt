package com.example.notfallapp.menubar.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.service.ServiceCallAlarm
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Activity where you can select a profile picture from the gallery or to take a photo now
 */
class SelectProfilPictureActivity : AppCompatActivity(), ICheckPermission {

    private val _IMAGE_DIRECTORY = "/profilPicture"
    private val GALLERY = 1
    private val CAMERA = 2

    private lateinit var imageUpload: ImageView
    private lateinit var btnSelectPicture: Button
    private lateinit var btnSavePicture: Button
    private lateinit var btnCancelProfilPicture: Button
    private lateinit var btnSos: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_profil_picture)

        initComponents()

        checkGalleryPermission(this, this)

        btnSelectPicture.setOnClickListener{
            showPictureDialog()
        }

        btnSavePicture.setOnClickListener{
            setResult(2)
            finish()
        }

        btnCancelProfilPicture.setOnClickListener{
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI: Uri? = data.data
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    saveImage(bitmap)
                    imageUpload.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == CAMERA && data != null) {
            val thumbnail: Bitmap = data.extras?.get(resources.getString(R.string.data)) as Bitmap
            imageUpload.setImageBitmap(thumbnail)
            saveImage(thumbnail)
        }
    }

    /**
     * save the image to the directory
     */
    private fun saveImage(myBitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + _IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.
        if (wallpaperDirectory.exists()) {
            wallpaperDirectory.delete()
        }
        wallpaperDirectory.mkdirs()
        try {
            val f = File(
                wallpaperDirectory, resources.getString(R.string.namePicture)
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf<String>(f.path),
                arrayOf(resources.getString(R.string.PictureType)),
                null
            )
            fo.close()
            Log.d(resources.getString(R.string.savePicture),
                  String.format(resources.getString(R.string.savePicturePath),f.absolutePath))
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    /**
     * opens the photo gallery of the device
     */
    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    /**
     * opens the camera
     */
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    /**
     * shows a dialog where the user can select gallery or camera
     */
    private fun showPictureDialog() {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        pictureDialog.setTitle(resources.getString(R.string.decide))
        val pictureDialogItems = arrayOf(
            resources.getString(R.string.pictureGalerie),
            resources.getString(R.string.takePicture)
        )
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun initComponents(){
        imageUpload = findViewById(R.id.imageUpload)
        btnSelectPicture = findViewById(R.id.btnSelectPicture)
        btnSavePicture = findViewById(R.id.btnSavePicture)
        btnCancelProfilPicture = findViewById(R.id.btnCancelProfilPicture)
        btnSos = findViewById(R.id.btn_sos)
        checkInternetGPSPermissions(this)

        btnSos.setOnClickListener{
            val intent = Intent(this, ServiceCallAlarm::class.java)
            this.startService(intent)
        }
    }

    companion object{
        /**
         * check if the permission are granted by the user
         */
        fun checkGalleryPermission(context: Context, activity: Activity){
            Dexter.withActivity(activity)
                .withPermissions(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.i(context.resources.getString(R.string.UserPermission),
                                context.resources.getString(R.string.UserPermissionGranted))
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener {
                }
                .onSameThread()
                .check()
        }
    }
}