package com.example.notfallapp.menubar.contact

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.checkPermission
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException

class SelectContactPictureActivity : AppCompatActivity(), checkPermission {

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
            Log.d(resources.getString(R.string.ContactPicture),
                  String.format(resources.getString(R.string.SelectContactPictureMessage),
                                resources.getString(R.string.SelectContactPicture)))
            choosePhotoFromGallary()
        }

        btnSaveContactPicture.setOnClickListener{
            Log.d(resources.getString(R.string.SaveContactPicture),
                  String.format(resources.getString(R.string.SaveContactPictureMessage),
                                resources.getString(R.string.SelectContactPicture)))
            if(responseIntent != null){
                setResult(1, responseIntent)
            }else{
                setResult(1)
            }
            finish()
        }

        btnCancelContactPicture.setOnClickListener{
            Log.d(resources.getString(R.string.CancelContactPicture),
                  String.format(resources.getString(R.string.CancelContactPictureMessage),
                                resources.getString(R.string.SelectContactPicture)))
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
                        responseIntent!!.putExtra(resources.getString(R.string.path), contentURI.path)
                    }
                }catch (e: IOException){
                    Log.e(resources.getString(R.string.image),
                          String.format(resources.getString(R.string.Image),
                                        resources.getString(R.string.SelectContactPicture), e.toString()))
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
                    Log.i(resources.getString(R.string.UserPermission),
                          resources.getString(R.string.UserPermissionGranted))
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
                Toast.makeText(applicationContext, resources.getString(R.string.someError), Toast.LENGTH_SHORT)
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
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)
    }
}