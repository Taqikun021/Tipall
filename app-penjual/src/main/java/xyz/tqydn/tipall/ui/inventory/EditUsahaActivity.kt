package xyz.tqydn.tipall.ui.inventory

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.databinding.ActivityEditUsahaBinding
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
    private lateinit var binding: ActivityEditUsahaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUsahaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        getDataUsaha()
        binding.imageProfil.setOnClickListener {
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
        binding.getLokasi.setOnClickListener {
            takeLocationIntent.launch(com.sucho.placepicker.Constants.PLACE_PICKER_REQUEST)
        }
        binding.buttonSimpan.setOnClickListener {
            val namaUsaha = binding.etNamaUsaha.text.toString().trim()
            val foto = imageUri.toString()
            alamats = binding.alamat.text.toString().trim()
            when {
                namaUsaha.isEmpty() -> {
                    binding.etNamaUsaha.error = "Nama tidak boleh kosong"
                    binding.etNamaUsaha.requestFocus()
                } binding.etLokasi.equals("") -> {
                    binding.etLokasi.error = "Ambil titik lokasi dengan mengetuk simbol lokasi"
                    binding.etLokasi.requestFocus()
                } else -> {
                    updateUsaha(namaUsaha, foto, latitude, longitude, alamats)
                }
            }
        }
    }

    private fun getDataUsaha() {
        val call: Call<GetInfoPenjual> = Constants.apiInterface.getInfoPenjual(preference.getValues("id_user"))
        call.enqueue(object : Callback<GetInfoPenjual> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<GetInfoPenjual>, response: Response<GetInfoPenjual>) {
                val ui: GetInfoPenjual? = response.body()
                if (ui?.status.toString() != "200") {
                    Toast.makeText(this@EditUsahaActivity, ui?.message, Toast.LENGTH_SHORT).show()
                } else {
                    binding.etNamaUsaha.setText(ui?.dist_data?.nama_usaha.toString())
                    val la = ui?.dist_data?.lat.toString()
                    val lo = ui?.dist_data?.lng.toString()
                    latitude = la
                    longitude = lo
                    alamats = ui?.dist_data?.alamat.toString()
                    binding.etLokasi.setText("$la, $lo")
                    binding.alamat.text = alamats
                    imageUri = Uri.parse(ui?.dist_data?.foto_usaha.toString())
                    Glide.with(this@EditUsahaActivity)
                        .load(imageUri)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.imageUsaha)
                    binding.imageUsaha.visibility = View.VISIBLE
                    binding.imageUsahaAwal.visibility = View.GONE
                    preference.setValues("id_penjual", ui?.dist_data?.id_penjual.toString())
                }
            }

            override fun onFailure(call: Call<GetInfoPenjual>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@EditUsahaActivity).create()
                val inflater = LayoutInflater.from(this@EditUsahaActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
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
                            .into(binding.imageUsaha)
                        binding.imageUsaha.visibility = View.VISIBLE
                        binding.imageUsahaAwal.visibility = View.GONE
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

    @SuppressLint("SetTextI18n")
    private val takeLocationIntent = registerForActivityResult(PlacePickerContract()) { result ->
        latitude = result[0]
        longitude = result[1]
        alamats = result[2]
        binding.etLokasi.setText("${result[0]}, ${result[1]}")
        binding.alamat.text = result[2]
    }
}