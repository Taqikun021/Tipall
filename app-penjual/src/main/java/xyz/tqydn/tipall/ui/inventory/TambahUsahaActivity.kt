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
import com.sucho.placepicker.Constants.PLACE_PICKER_REQUEST
import kotlinx.android.synthetic.main.activity_tambah_usaha.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.model.DefaultResponse
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.SharedPreference
import xyz.tqydn.tipall.utils.contracts.ImageCameraContract
import xyz.tqydn.tipall.utils.contracts.PlacePickerContract
import java.io.ByteArrayOutputStream
import java.util.*

class TambahUsahaActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var imageUri: Uri
    private lateinit var alamats: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_usaha)
        preference = SharedPreference(this)

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
            takeLocationIntent.launch(PLACE_PICKER_REQUEST)
        }

        buttonSimpan.setOnClickListener {
            val nama = etNamaUsaha.text.toString().trim()
            val foto = imageUri.toString()
            alamats = alamat.text.toString().trim()

            when {
                nama.isEmpty() -> {
                    etNamaUsaha.error = "Nama tidak boleh kosong"
                    etNamaUsaha.requestFocus()
                } etLokasi.equals("") -> {
                    etLokasi.error = "Ambil titik lokasi dengan mengetuk simbol lokasi"
                    etLokasi.requestFocus()
                } else -> {
                    tambahUsaha(nama, foto, latitude, longitude, alamats)
                }
            }
        }
    }

    private fun tambahUsaha(nama: String, foto: String, lat: String, long: String, alamat: String) {
        val call: Call<DefaultResponse> = Constants.apiInterface
                .tambahUsaha(preference.getValues("id_user"), nama, foto, lat, long, alamat)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val ui: DefaultResponse? = response.body()
                if (ui?.status.toString() == "201") {
                    val intent = Intent().apply {
                        putExtra(Constants.TITLE, ui?.message.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@TambahUsahaActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance().reference.child("usaha/" + UUID.randomUUID().toString())
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()
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