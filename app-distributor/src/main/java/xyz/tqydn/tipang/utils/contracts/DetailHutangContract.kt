package xyz.tqydn.tipang.utils.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import xyz.tqydn.tipang.utils.Constants

class DetailHutangContract: ActivityResultContract<Int, String>(){
    override fun createIntent(context: Context, input: Int?): Intent {
        return Constants.detailHutang(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        val data = intent?.getStringExtra(Constants.TITLE)
        return if (resultCode == Activity.RESULT_OK && data != null) data
        else null
    }
}