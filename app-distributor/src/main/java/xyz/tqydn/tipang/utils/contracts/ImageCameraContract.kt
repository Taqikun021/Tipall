package xyz.tqydn.tipang.utils.contracts

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class ImageCameraContract: ActivityResultContract<Int, Bitmap>() {
    override fun createIntent(context: Context, input: Int?): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
        return if(resultCode == RESULT_OK) intent?.extras?.get("data") as Bitmap
        else null
    }
}