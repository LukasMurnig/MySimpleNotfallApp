package com.example.notfallapp.menubar.settings

import android.app.Activity
import android.app.AlertDialog
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.checkPermission
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


class SelectProfilPictureActivity : AppCompatActivity(), checkPermission {

    private val IMAGE_DIRECTORY = "/profilPicture"
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

        requestMultiplePermissions()

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
                    val path: String? = saveImage(bitmap)
                    Toast.makeText(this, resources.getString(R.string.Imagesaved), Toast.LENGTH_SHORT).show()
                    imageUpload.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, resources.getString(R.string.Failed), Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CAMERA && data != null) {
            val thumbnail: Bitmap = data.extras.get(resources.getString(R.string.data)) as Bitmap
            imageUpload.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this, resources.getString(R.string.Imagesaved), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(myBitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY
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

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

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

    private fun requestMultiplePermissions(){
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Log.i(resources.getString(R.string.UserPermission),
                              resources.getString(R.string.UserPermissionGranted))
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
                Toast.makeText(applicationContext, resources.getString(R.string.someError), Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    private fun initComponents(){
        imageUpload = findViewById(R.id.imageUpload)
        btnSelectPicture = findViewById(R.id.btnSelectPicture)
        btnSavePicture = findViewById(R.id.btnSavePicture)
        btnCancelProfilPicture = findViewById(R.id.btnCancelProfilPicture)
        btnSos = findViewById(R.id.btn_sos)
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)

        btnSos.setOnClickListener{
            val intent = Intent(this, ServiceCallAlarm::class.java)
            this.startService(intent)
        }
    }
}