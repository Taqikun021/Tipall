package xyz.tqydn.tipang.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_usaha.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DefaultResponse
import xyz.tqydn.tipang.model.GetDistInfo
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.ImageCameraContract
import xyz.tqydn.tipang.utils.contracts.PlacePickerContract
import java.io.ByteArrayOutputStream
import java.util.*

@SuppressLint("SetTextI18n")
class EditUsahaActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var imageUri: Uri
    private lateinit var alamats: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_usaha)
        preference = SharedPreference(this)

        getDistInfo()
        imageProfil.setOnClickListener {
            val photoDialog = MaterialAlertDialogBuilder(this).create()
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.photo_dialog, null)
            photoDialog.setCancelable(true)
            photoDialog.setView(dialogView)
            val kamera = dialogView.findViewById(R.id.ambilPhoto) as LinearLayout
            val file = dialogView.findViewById(R.id.pilihFile) as LinearLayout

            kamera.setOnClickListener {
                bukaKamera.launch(Constants.REQUEST_IMAGE_CAPTURE)
                photoDialog.dismiss()
            }
            file.setOnClickListener {
                pilihImage.launch("image/*")
                photoDialog.dismiss()
            }
            photoDialog.show()
        }

        getLokasi.setOnClickListener {
            takeLocationIntent.launch(com.sucho.placepicker.Constants.PLACE_PICKER_REQUEST)
        }

        buttonSimpan.setOnClickListener {
            val namaUsaha = etNamaUsaha.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val foto = imageUri.toString()
            alamats = alamat.text.toString().trim()

            when { namaUsaha.isEmpty() -> {
                etNamaUsaha.error = "Nama tidak boleh kosong"
                etNamaUsaha.requestFocus()
            } desc.isEmpty() -> {
                etDesc.error = "Deskripsi tidak boleh kosong"
                etDesc.requestFocus()
            } etLokasi.equals("") -> {
                etLokasi.error = "Ambil titik lokasi dengan mengetuk simbol lokasi"
                etLokasi.requestFocus()
            } else -> {
                updateUsaha(namaUsaha, foto, latitude, longitude, alamats, desc)
            }
            }
        }
    }

    private fun updateUsaha(nama: String, foto: String, lat: String, long: String, alamat: String, desc: String) {
        val call: Call<DefaultResponse> = Constants.apiInterface
            .updateUsaha(preference.getValues("id_distributor"), nama, foto, lat, long, alamat, desc)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val ui: DefaultResponse? = response.body()
                if (ui?.status.toString() == "201") {
                    preference.setValues("lat", lat)
                    preference.setValues("lng", long)
                    val intent = Intent().apply {
                        putExtra(Constants.TITLE, ui?.message.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@EditUsahaActivity).create()
                val inflater = LayoutInflater.from(this@EditUsahaActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getDistInfo() {
        val call: Call<GetDistInfo> = Constants.apiInterface.getDistInfo(preference.getValues("id_user"))
        call.enqueue(object : Callback<GetDistInfo> {
            override fun onResponse(call: Call<GetDistInfo>, response: Response<GetDistInfo>) {
                val ui: GetDistInfo? = response.body()
                if (ui?.status.toString() != "200") {
                    Toast.makeText(this@EditUsahaActivity, ui?.message, Toast.LENGTH_SHORT).show()
                } else {
                    etNamaUsaha.setText(ui?.dist_data?.nama_usaha.toString())
                    etDesc.setText(ui?.dist_data?.desc.toString())
                    val la = ui?.dist_data?.lat.toString()
                    val lo = ui?.dist_data?.lng.toString()
                    latitude = la
                    longitude = lo
                    alamats = ui?.dist_data?.alamat.toString()
                    etLokasi.setText("$la, $lo")
                    alamat.text = alamats
                    imageUri = Uri.parse(ui?.dist_data?.foto_usaha.toString())
                    Glide.with(this@EditUsahaActivity)
                        .load(imageUri)
                        .apply(RequestOptions.centerCropTransform())
                        .into(imageUsaha)

                    imageUsaha.visibility = View.VISIBLE
                    imageUsahaAwal.visibility = View.GONE

                    preference.setValues("id_distributor", ui?.dist_data?.id_distributor.toString())
                }
            }

            override fun onFailure(call: Call<GetDistInfo>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@EditUsahaActivity).create()
                val inflater = LayoutInflater.from(this@EditUsahaActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap?) {
        val image = ByteArrayOutputStream().toByteArray()
        val storageRef = FirebaseStorage.getInstance().reference.child("usaha/" + UUID.randomUUID().toString())
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream())
        val upload = storageRef.putBytes(image)

        upload.addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.centerCropTransform())
                            .into(imageUsaha)

                        imageUsaha.visibility = View.VISIBLE
                        imageUsahaAwal.visibility = View.GONE
                    }
                }
            } else {
                uploadTask.exception?.let {
                    Toast.makeText(this, it.message!!, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val takeLocationIntent = registerForActivityResult(PlacePickerContract()) { result ->
        latitude = result[0]
        longitude = result[1]
        alamats = result[2]
        etLokasi.setText("${result[0]}, ${result[1]}")
        alamat.text = result[2]
    }

    private val bukaKamera = registerForActivityResult(ImageCameraContract()) { bitmap ->
        uploadImageAndSaveUri(bitmap)
    }

    private val pilihImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val input = this.contentResolver.openInputStream(uri)
        uploadImageAndSaveUri(BitmapFactory.decodeStream(input))
    }
}