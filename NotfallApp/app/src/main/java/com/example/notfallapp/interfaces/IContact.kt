package com.example.notfallapp.interfaces

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import java.io.IOException

interface IContact {

    fun setImage(data: Intent, context: Context, image: ImageButton): String {
        val path =  "content://media" + data.getStringExtra("path")
        if(path != null){
            try{
                val uri = Uri.parse(path)
                val bitmap =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                image.background = BitmapDrawable(context.resources, bitmap)
            }catch (e: IOException){
                Log.e(context.resources.getString(R.string.image),
                    String.format(context.resources.getString(R.string.Image),
                        context.resources.getString(R.string.AddContact), e.toString()))
            }
        }
        return path
    }

    fun initCancelButton(context: Context, btnCancel: Button, builder: AlertDialog.Builder){
        btnCancel.setOnClickListener {
            Log.d(context.resources.getString(R.string.CancelButton),
                String.format(context.resources.getString(R.string.CancelButtonClicked),
                    context.resources.getString(R.string.AddContact)))
            sureDialog(context, builder)
            val alert = builder.create()
            alert.show()
        }
    }

    fun sureDialog(context: Context, builder: AlertDialog.Builder){
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(context.resources.getString(R.string.sureStopCreatingContact))

        builder.setPositiveButton(context.resources.getString(R.string.Yes)) { dialog, _ ->
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton(context.resources.getString(R.string.No)) { dialog, _ ->
            dialog.dismiss()
        }
    }
}