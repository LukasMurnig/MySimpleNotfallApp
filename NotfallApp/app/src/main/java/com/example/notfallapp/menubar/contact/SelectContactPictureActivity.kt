package com.example.notfallapp.menubar.contact

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException

class SelectContactPictureActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private lateinit var btnSelectContactPicture: Button
    private lateinit var btnSaveContactPicture: Button
    private lateinit var btnCancelContactPicture: Button

    private var responseIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contact_picture)

        initComponents()

        requestPermission()

        btnSelectContactPicture.setOnClickListener{
            choosePhotoFromGallary()
        }

        btnSaveContactPicture.setOnClickListener{
            if(responseIntent != null){
                setResult(1, responseIntent)
            }else{
                setResult(1)
            }
            finish()
        }

        btnCancelContactPicture.setOnClickListener{
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_CANCELED) {
            return
        }
        if (requestCode === 2) {
            if (data != null) {
                val contentURI: Uri? = data.data
                responseIntent = Intent()
                try{
                    if(contentURI!=null){
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, contentURI)
                        image.setImageBitmap(bitmap)
                        responseIntent!!.putExtra("path", contentURI.path)
                    }
                }catch (e: IOException){
                    Log.e("image", e.printStackTrace().toString())
                }
            }
        }
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, 2)
    }

    private fun requestPermission(){
        Dexter.withActivity(this)
            .withPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Log.i("UserPermission", "External Storage permission for changing contact picture is granted by user!")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }
            }).withErrorListener {
                Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    private fun initComponents(){
        image = findViewById(R.id.contactImageUpload)
        btnSelectContactPicture = findViewById(R.id.btnSelectContactPicture)
        btnSaveContactPicture = findViewById(R.id.btnSaveContactPicture)
        btnCancelContactPicture = findViewById(R.id.btnCancelContactPicture)
    }
}