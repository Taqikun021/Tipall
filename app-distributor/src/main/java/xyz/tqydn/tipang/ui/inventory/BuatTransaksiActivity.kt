package xyz.tqydn.tipang.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.BarangPagerAdapter
import xyz.tqydn.tipang.databinding.ActivityBuatTransaksiBinding
import xyz.tqydn.tipang.model.*
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference

@SuppressLint("SetTextI18n")
class BuatTransaksiActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private var hargaBarang: Double = 0.0
    private var stok: Int = 0
    private lateinit var idPenjual: String
    private lateinit var idDistributor: String
    private lateinit var idBarang: String
    private lateinit var binding: ActivityBuatTransaksiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuatTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        var jumlahBarang = 0
        getPenjualInfo()
        getRatingPenjual()
        getBarang()
        binding.kurang.setOnClickListener {
            if (jumlahBarang == 0){
                binding.jumlah.error = "Jumlah harus positif"
            } else {
                jumlahBarang -= 1
            }
            binding.jumlah.setText(jumlahBarang.toString())
            binding.jumlah.requestFocus()
            binding.total.text = Constants.formatRupiah(jumlahBarang*hargaBarang)
        }
        binding.tambah.setOnClickListener {
            if (jumlahBarang >= stok){
                binding.jumlah.error = "Stok tidak mencukupi"
            } else {
                jumlahBarang += 1
            }
            binding.jumlah.setText(jumlahBarang.toString())
            binding.jumlah.requestFocus()
            binding.total.text = Constants.formatRupiah(jumlahBarang*hargaBarang)
        }
        binding.kirimTawaran.setOnClickListener {
            val jml = binding.jumlah.text.toString().trim()
            val hasil = (hargaBarang*jumlahBarang).toString()

            if(!Constants.isNumber(jml)) {
                binding.jumlah.error = "Jumlah harus angka"
                binding.jumlah.requestFocus()
            } else if(jml.toInt() < 1) {
                binding.jumlah.error = "Jumlah harus terisi minimal 1"
                binding.jumlah.requestFocus()
            } else if(jml.toInt() > stok) {
                binding.jumlah.error = "Stok tidak mencukupi"
                binding.jumlah.requestFocus()
            } else {
                buatTransaksi(jml, hasil)
            }
        }
    }

    private fun buatTransaksi(jml: String, hasil: String) {
        val call: Call<DefaultResponse> = Constants.apiInterface.buatTransaksi(idPenjual, idDistributor, idBarang, Constants.kodeTransaksi(), jml, hasil, 0, Constants.status1)
        call.enqueue(object: Callback<DefaultResponse>{
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.body()?.status.toString() == "201") {
                    Toast.makeText(this@BuatTransaksiActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                } else {
                    val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                    val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                }
                val intent = Intent().apply {
                    putExtra(Constants.TITLE, response.body()?.message.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getBarang() {
        val call: Call<Barang> = Constants.apiInterface.getBarang(preference.getValues("id_distributor"))
        call.enqueue(object : Callback<Barang> {
            override fun onResponse(call: Call<Barang>, response: Response<Barang>) {
                val item: Barang? = response.body()
                val listBarang = item?.dataBarang
                if (item!!.jumlahData > 0) {
                    binding.viewPager.adapter = BarangPagerAdapter(listBarang)
                    binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            val a = listBarang?.get(position)
                            stok = a?.jumlah_stok!!.toInt()
                            idBarang = a.id_barang
                            hargaBarang = a.harga_awal.toDouble()
                        }
                    })
                } else {
                    val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                    val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                }
            }

            override fun onFailure(call: Call<Barang>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getRatingPenjual() {
        val call: Call<Rating> = Constants.apiInterface.getRatingPenjual(preference.getValues("penjual_click"))
        call.enqueue(object : Callback<Rating> {
            override fun onResponse(call: Call<Rating>, response: Response<Rating>) {
                val item: Rating? = response.body()
                if (response.code() == 200) {
                    binding.ratingPenjual.text = "%.2f".format(item?.rating_penjual!!.toDouble())
                    binding.transaksi.text = "Dari ${item.jumlah_transaksi} Transaksi"
                } else {
                    val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                    val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                }
            }

            override fun onFailure(call: Call<Rating>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getPenjualInfo() {
        val call: Call<DataPenjual> = Constants.apiInterface.getPenjualInfo(preference.getValues("penjual_click"))
        call.enqueue(object : Callback<DataPenjual> {
            override fun onResponse(call: Call<DataPenjual>, response: Response<DataPenjual>) {
                val item: DataPenjual? = response.body()
                if (response.code() == 200) {
                    val img = Uri.parse(item?.foto)
                    val distance = Constants.hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        item?.lat!!.toDouble(),
                        item.lng.toDouble()
                    )

                    idPenjual = item.id_penjual
                    idDistributor = preference.getValues("id_distributor")!!
                    binding.namaUsaha.text = item.nama_usaha
                    binding.alamat.text = item.alamat
                    binding.jarak.text = "${"%.2f".format(distance)} km"
                    if (item.jenis_kelamin == "Perempuan") {
                        binding.namaPemilik.text = "Ibu ${item.username}"
                    } else {
                        binding.namaPemilik.text = "Bapak ${item.username}"
                    }
                    Glide.with(this@BuatTransaksiActivity)
                        .load(img)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imagePenjual)
                } else {
                    val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                    val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                }
            }

            override fun onFailure(call: Call<DataPenjual>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }
}