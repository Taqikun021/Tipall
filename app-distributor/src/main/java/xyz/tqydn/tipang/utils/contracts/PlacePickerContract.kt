package xyz.tqydn.tipang.utils.contracts

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.utils.SharedPreference

class PlacePickerContract: ActivityResultContract<Int, List<String>>() {

    lateinit var preference: SharedPreference

    override fun createIntent(context: Context, input: Int?): Intent {
        preference = SharedPreference(context)
        val lat = preference.getValues("lat_saya")?.toDouble()
        val lng = preference.getValues("lng_saya")?.toDouble()
        return PlacePicker.IntentBuilder()
            .setLatLong(lat!!, lng!!)
            .showLatLong(true)
            .setMapZoom(18.0f)
            .setAddressRequired(true)
            .setMarkerDrawable(R.drawable.ic_marker)
            .setFabColor(R.color.colorPrimaryDark)
            .setPrimaryTextColor(R.color.textDiAwan)
            .setSecondaryTextColor(R.color.textGrey)
            .setBottomViewColor(R.color.colorPrimary)
            .setMapType(MapType.NORMAL)
            .hideLocationButton(true)
            .build(context as Activity)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<String>? {
        val addressData = intent?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
        val lat = addressData?.latitude.toString()
        val lng = addressData?.longitude.toString()
        val alamat = addressData?.addressList?.get(0)?.getAddressLine(0).toString()
        val data = listOf(lat, lng, alamat)
        return if (resultCode == RESULT_OK && addressData != null) data
        else null
    }
}