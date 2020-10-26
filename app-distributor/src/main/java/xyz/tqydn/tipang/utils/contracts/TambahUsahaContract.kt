package xyz.tqydn.tipang.utils.contracts

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import xyz.tqydn.tipang.utils.Constants

class TambahUsahaContract: ActivityResultContract<Int, String>() {
    override fun createIntent(context: Context, input: Int?): Intent {
        return Constants.tambahUsaha(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        val data = intent?.getStringExtra(Constants.TITLE)
        return if (resultCode == RESULT_OK && data != null) data
        else null
    }
}