package xyz.tqydn.tipang.ui.inventory

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.databinding.ActivityEditProfilBinding
import xyz.tqydn.tipang.model.DefaultResponse
import xyz.tqydn.tipang.model.GetUserInfo
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.ImageCameraContract
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*

class EditProfilActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var imageUri: Uri
    private lateinit var binding: ActivityEditProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        getUserInfo()
        val item = listOf("Laki laki", "Perempuan")
        val adapter = ArrayAdapter(this, R.layout.list_kelamin, item)
        (binding.LayoutKelamin.editText as? AutoCompleteTextView)?.setAdapter(adapter)
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
        binding.buttonSimpan.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val kelamin = binding.etKelamin.text.toString().trim()
            val noHP = binding.etHape.text.toString().trim()
            val foto = imageUri.toString()
            when {
                nama.isEmpty() -> {
                    binding.etNama.error = "Nama Tidak Boleh Kosong"
                    binding.etNama.requestFocus()
                } email.isEmpty() -> {
                    binding.etEmail.error = "Email Tidak Boleh Kosong"
                    binding.etEmail.requestFocus()
                } noHP.isEmpty() -> {
                    binding.etHape.error = "Silahkan isi Nomor HP"
                    binding.etHape.requestFocus()
                } else -> {
                    updateProfil(email, nama, kelamin, noHP, foto)
                }
            }
        }
    }

    private fun updateProfil(email: String, nama: String, kelamin: String, noHP: String, foto: String) {
        val call: Call<DefaultResponse> = Constants.apiInterface
            .updateProfil(preference.getValues("id_user"), email, nama, kelamin, noHP, foto)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@EditProfilActivity).create()
                val inflater = LayoutInflater.from(this@EditProfilActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }

            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val defaultResponse: DefaultResponse? = response.body()
                val intent = Intent().apply {
                    putExtra(Constants.TITLE, defaultResponse?.message.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

    private fun getUserInfo() {
        binding.loading.visibility = View.VISIBLE
        val call : Call<GetUserInfo> = Constants.apiInterface.getUserInfo("Bearer " + preference.getValues("token"))
        call.enqueue(object : Callback<GetUserInfo> {
            override fun onFailure(call: Call<GetUserInfo>, t: Throwable) {
                binding.loading.visibility = View.GONE
                val photoDialog = MaterialAlertDialogBuilder(this@EditProfilActivity).create()
                val inflater = LayoutInflater.from(this@EditProfilActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }

            override fun onResponse(call: Call<GetUserInfo>, response: Response<GetUserInfo>) {
                val ui: GetUserInfo? = response.body()
                if (ui?.status.toString() != "200") {
                    binding.loading.visibility = View.GONE
                    val photoDialog = MaterialAlertDialogBuilder(this@EditProfilActivity).create()
                    val inflater = LayoutInflater.from(this@EditProfilActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                } else {
                    binding.etNama.setText(ui?.user?.username.toString())
                    binding.etEmail.setText(ui?.user?.email.toString())
                    binding.etHape.setText(ui?.user?.no_hp.toString())
                    imageUri = Uri.parse(ui?.user?.foto.toString())
                    binding.etKelamin.setText(ui?.user?.jenis_kelamin)
                    try {
                        Glide.with(this@EditProfilActivity)
                            .load(imageUri)
                            .apply(RequestOptions.centerCropTransform())
                            .into(binding.imageUsaha)
                    } catch (e: FileNotFoundException) {
                        Toast.makeText(this@EditProfilActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                        binding.imageUsaha.setImageResource(R.drawable.ic_foto_profil)
                    }
                    binding.loading.visibility = View.GONE
                    binding.imageUsahaAwal.visibility = View.GONE
                    binding.imageUsaha.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap?) {
        binding.loading.visibility = View.VISIBLE
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance().reference.child("profil/" + UUID.randomUUID().toString())
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()
        val upload = storageRef.putBytes(image)
        upload.addOnCompleteListener { uploadtask ->
            if (uploadtask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        binding.loading.visibility = View.GONE
                        Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imageProfil)
                    }
                }
            } else {
                uploadtask.exception?.let {
                    binding.loading.visibility = View.GONE
                    Toast.makeText(this, it.message!!, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val bukaKamera  = registerForActivityResult(ImageCameraContract()) { bitmap ->
        uploadImageAndSaveUri(bitmap)
    }

    private val pilihImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val input = this.contentResolver.openInputStream(uri)
        uploadImageAndSaveUri(BitmapFactory.decodeStream(input))
    }
}