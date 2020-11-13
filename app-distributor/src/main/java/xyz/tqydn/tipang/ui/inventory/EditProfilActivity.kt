package xyz.tqydn.tipang.ui.inventory

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profil.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DefaultResponse
import xyz.tqydn.tipang.model.GetUserInfo
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.ImageCameraContract
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*

class EditProfilActivity : AppCompatActivity() {

    lateinit var preference: SharedPreference
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)
        preference = SharedPreference(this)
        getUserInfo()
        val item = listOf("Laki laki", "Perempuan")
        val adapter = ArrayAdapter(this, R.layout.list_kelamin, item)
        (LayoutKelamin.editText as? AutoCompleteTextView)?.setAdapter(adapter)
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
        buttonSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val kelamin = etKelamin.text.toString().trim()
            val noHP = etHape.text.toString().trim()
            val foto = imageUri.toString()
            when {
                nama.isEmpty() -> {
                    etNama.error = "Nama Tidak Boleh Kosong"
                    etNama.requestFocus()
                } email.isEmpty() -> {
                    etEmail.error = "Email Tidak Boleh Kosong"
                    etEmail.requestFocus()
                } noHP.isEmpty() -> {
                    etHape.error = "Silahkan isi Nomor HP"
                    etHape.requestFocus()
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
        val call : Call<GetUserInfo> = Constants.apiInterface.getUserInfo("Bearer " + preference.getValues("token"))
        call.enqueue(object : Callback<GetUserInfo> {
            override fun onFailure(call: Call<GetUserInfo>, t: Throwable) {
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
                    Toast.makeText(this@EditProfilActivity, ui?.message, Toast.LENGTH_LONG).show()
                } else {
                    etNama.setText(ui?.user?.username.toString())
                    etEmail.setText(ui?.user?.email.toString())
                    etHape.setText(ui?.user?.no_hp.toString())
                    imageUri = Uri.parse(ui?.user?.foto.toString())
                    try {
                        Glide.with(this@EditProfilActivity)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageProfil)
                    } catch (e: FileNotFoundException) {
                        Toast.makeText(this@EditProfilActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                        imageProfil.setImageResource(R.drawable.ic_foto_profil)
                    }
                    preference.setValues("id_user", ui?.user?.id_user.toString())
                }
            }
        })
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap?) {
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
                        Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageProfil)
                    }
                }
            } else {
                uploadtask.exception?.let {
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