package xyz.tqydn.tipang.ui.inventory

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_barang.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DataBarang
import xyz.tqydn.tipang.model.DefaultResponse
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference
import xyz.tqydn.tipang.utils.contracts.ImageCameraContract
import java.io.ByteArrayOutputStream
import java.util.*

class EditBarangActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_barang)
        preference = SharedPreference(this)

        getBarangInfo()
        imageBarang.setOnClickListener {
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

        buttonHapus.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(this).create()
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.alert_hapus, null)
            alertDialog.setCancelable(true)
            alertDialog.setView(dialogView)
            val yakin = dialogView.findViewById(R.id.yakin) as Button
            val batal = dialogView.findViewById(R.id.batal) as Button
            yakin.setOnClickListener {
                hapusBarang()
                alertDialog.dismiss()
            }

            batal.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

        buttonSimpan.setOnClickListener{
            val nama = etNamaBarang.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val foto = imageUri.toString()
            val stok = etStok.text.toString().trim()
            val harga = etHarga.text.toString().trim()
            val hargaJual = etHargaJual.text.toString().trim()

            when {
                nama.isEmpty() -> {
                    etNamaBarang.error = "Nama Barang tidak boleh kosong"
                    etNamaBarang.requestFocus()
                }
                desc.isEmpty() -> {
                    etDesc.error = "Berikan deskripsi tentang barang anda"
                    etDesc.requestFocus()
                }
                stok.toInt() < 0 -> {
                    etStok.error = "Stok minimum 0"
                    etStok.requestFocus()
                }
                harga >= hargaJual -> {
                    etHargaJual.error = "Berikan selisih harga untuk keuntungan anda"
                    etHargaJual.requestFocus()
                }
                harga.toInt() < 1 -> {
                    etHarga.error = "Harga minimum Rp. 1"
                    etHarga.requestFocus()
                }
                else -> {
                    editBarang(nama, foto, stok, desc, harga, hargaJual)
                }
            }
        }
    }

    private fun editBarang(nama: String, foto: String, stok: String, desc: String, harga: String, hargaJual: String) {
        val call: Call<DefaultResponse> = Constants.apiInterface
            .editBarang(preference.getValues("barang_click"), nama, foto, stok, desc, harga, hargaJual)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val data = response.body()
                val intent = Intent().apply {
                    putExtra(Constants.TITLE, data?.message.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@EditBarangActivity).create()
                val inflater = LayoutInflater.from(this@EditBarangActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun hapusBarang() {
        val call: Call<DefaultResponse> = Constants.apiInterface.hapusBarang(preference.getValues("barang_click"))
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val data = response.body()
                Toast.makeText(this@EditBarangActivity, data?.message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@EditBarangActivity).create()
                val inflater = LayoutInflater.from(this@EditBarangActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getBarangInfo() {
        val call: Call<DataBarang> = Constants.apiInterface.getInfoBarang(preference.getValues("barang_click"))
        call.enqueue(object : Callback<DataBarang> {
            override fun onResponse(call: Call<DataBarang>, response: Response<DataBarang>) {
                val data = response.body()
                imageUri = Uri.parse(data?.foto_barang)
                etNamaBarang.setText(data?.nama_barang)
                etDesc.setText(data?.deskripsi_produk)
                etStok.setText(data?.jumlah_stok)
                etHarga.setText(data?.harga_awal)
                etHargaJual.setText(data?.harga_jual)
                Glide.with(this@EditBarangActivity)
                    .load(imageUri)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageBarangFix)

                imageBarangAwal.visibility = View.GONE
                imageBarangFix.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<DataBarang>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@EditBarangActivity).create()
                val inflater = LayoutInflater.from(this@EditBarangActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance().reference.child("barang/" + UUID.randomUUID().toString())
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
                            .into(imageBarangFix)

                        imageBarangFix.visibility = View.VISIBLE
                        imageBarangAwal.visibility = View.GONE
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
}