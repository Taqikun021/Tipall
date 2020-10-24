package xyz.tqydn.tipall.ui.inventory

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.model.DefaultResponse
import xyz.tqydn.tipall.model.GetInfoPenjual
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.ImageCameraContract
import xyz.tqydn.tipall.utils.contracts.PlacePickerContract
import java.io.ByteArrayOutputStream
import java.util.*

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

        getDataUsaha()
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
            val foto = imageUri.toString()
            alamats = alamat.text.toString().trim()

            when {
                namaUsaha.isEmpty() -> {
                    etNamaUsaha.error = "Nama tidak boleh kosong"
                    etNamaUsaha.requestFocus()
                } etLokasi.equals("") -> {
                    etLokasi.error = "Ambil titik lokasi dengan mengetuk simbol lokasi"
                    etLokasi.requestFocus()
                } else -> {
                    updateUsaha(namaUsaha, foto, latitude, longitude, alamats)
                }
            }
        }
    }

    private fun getDataUsaha() {
        val call: Call<GetInfoPenjual> = Constants.apiInterface.getInfoPenjual(preference.getValues("id_user"))
        call.enqueue(object : Callback<GetInfoPenjual> {
            override fun onResponse(call: Call<GetInfoPenjual>, response: Response<GetInfoPenjual>) {
                val ui: GetInfoPenjual? = response.body()
                if (ui?.status.toString() != "200") {
                    Toast.makeText(this@EditUsahaActivity, ui?.message, Toast.LENGTH_SHORT).show()
                } else {
                    etNamaUsaha.setText(ui?.dist_data?.nama_usaha.toString())
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

                    preference.setValues("id_penjual", ui?.dist_data?.id_penjual.toString())
                }
            }

            override fun onFailure(call: Call<GetInfoPenjual>, t: Throwable) {
                Toast.makeText(this@EditUsahaActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUsaha(nama: String, foto: String, lat: String, long: String, alamat: String) {
        val call: Call<DefaultResponse> = Constants.apiInterface
            .updateUsaha(preference.getValues("id_penjual"), nama, foto, lat, long, alamat)
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
                Toast.makeText(this@EditUsahaActivity, t.message, Toast.LENGTH_SHORT).show()
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

    private val bukaKamera = registerForActivityResult(ImageCameraContract()) { bitmap ->
        uploadImageAndSaveUri(bitmap)
    }

    private val pilihImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val input = this.contentResolver.openInputStream(uri)
        uploadImageAndSaveUri(BitmapFactory.decodeStream(input))
    }

    private val takeLocationIntent = registerForActivityResult(PlacePickerContract()) { result ->
        latitude = result[0]
        longitude = result[1]
        alamats = result[2]
        etLokasi.setText("${result[0]}, ${result[1]}")
        alamat.text = result[2]
    }
}