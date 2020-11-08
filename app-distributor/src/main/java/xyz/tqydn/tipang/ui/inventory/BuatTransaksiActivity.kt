package xyz.tqydn.tipang.ui.inventory

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_buat_transaksi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.BarangPagerAdapter
import xyz.tqydn.tipang.model.*
import xyz.tqydn.tipang.utils.Constants.Companion.TITLE
import xyz.tqydn.tipang.utils.Constants.Companion.status1
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipang.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipang.utils.Constants.Companion.isNumber
import xyz.tqydn.tipang.utils.Constants.Companion.kodeTransaksi
import xyz.tqydn.tipang.utils.SharedPreference

class BuatTransaksiActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private var hargaBarang: Double = 0.0
    private var stok: Int = 0
    private lateinit var id_penjual: String
    private lateinit var id_distributor: String
    private lateinit var id_barang: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_transaksi)
        preference = SharedPreference(this)
        var jumlahBarang = 0

        getPenjualInfo()
        getRatingPenjual()
        getBarang()
        kurang.setOnClickListener {
            if (jumlahBarang == 0){
                jumlah.error = "Jumlah harus positif"
            } else {
                jumlahBarang -= 1
            }
            jumlah.setText(jumlahBarang.toString())
            jumlah.requestFocus()
            total.text = formatRupiah(jumlahBarang*hargaBarang)
        }
        tambah.setOnClickListener {
            if (jumlahBarang >= stok){
                jumlah.error = "Stok tidak mencukupi"
            } else {
                jumlahBarang += 1
            }
            jumlah.setText(jumlahBarang.toString())
            jumlah.requestFocus()
            total.text = formatRupiah(jumlahBarang*hargaBarang)
        }
        kirimTawaran.setOnClickListener {
            val jml = jumlah.text.toString().trim()
            val hasil = (hargaBarang*jumlahBarang).toString()

            if(!isNumber(jml)) {
                jumlah.error = "Jumlah harus angka"
                jumlah.requestFocus()
            } else if(jml.toInt() < 1) {
                jumlah.error = "Jumlah harus terisi minimal 1"
                jumlah.requestFocus()
            } else if(jml.toInt() > stok) {
                jumlah.error = "Stok tidak mencukupi"
                jumlah.requestFocus()
            } else {
                buatTransaksi(jml, hasil)
            }
        }
    }

    private fun buatTransaksi(jml: String, hasil: String) {
        val call: Call<DefaultResponse> = apiInterface.buatTransaksi(id_penjual, id_distributor, id_barang, kodeTransaksi(), jml, hasil, 0, status1)
        call.enqueue(object: Callback<DefaultResponse>{
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.body()?.status!!.equals(201)) {
                    Toast.makeText(this@BuatTransaksiActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
                val intent = Intent().apply {
                    putExtra(TITLE, response.body()?.message.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getBarang() {
        val call: Call<Barang> = apiInterface.getBarang(preference.getValues("id_distributor"))
        call.enqueue(object : Callback<Barang> {
            override fun onResponse(call: Call<Barang>, response: Response<Barang>) {
                val item: Barang? = response.body()
                val listBarang = item?.dataBarang
                if (item!!.jumlahData > 0) {
                    viewPager.adapter = BarangPagerAdapter(listBarang)
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            val a = listBarang?.get(position)
                            stok = a?.jumlah_stok!!.toInt()
                            id_barang = a.id_barang
                            hargaBarang = a.harga_awal.toDouble()
                            //Toast.makeText(this@BuatTransaksiActivity, "Kamu menawarkan $id_barang", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(call: Call<Barang>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRatingPenjual() {
        val call: Call<Rating> = apiInterface.getRatingPenjual(preference.getValues("penjual_click"))
        call.enqueue(object : Callback<Rating> {
            override fun onResponse(call: Call<Rating>, response: Response<Rating>) {
                val item: Rating? = response.body()
                if (response.code() == 200) {
                    ratingPenjual.text = "%.2f".format(item?.rating_penjual!!.toDouble())
                    transaksi.text = "Dari ${item.jumlah_transaksi} Transaksi"
                }
            }

            override fun onFailure(call: Call<Rating>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPenjualInfo() {
        val call: Call<DataPenjual> = apiInterface.getPenjualInfo(preference.getValues("penjual_click"))
        call.enqueue(object : Callback<DataPenjual> {
            override fun onResponse(call: Call<DataPenjual>, response: Response<DataPenjual>) {
                val item: DataPenjual? = response.body()
                if (response.code() == 200) {
                    val img = Uri.parse(item?.foto)
                    val distance = hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        item?.lat!!.toDouble(),
                        item.lng.toDouble()
                    )

                    id_penjual = item.id_penjual
                    id_distributor = preference.getValues("id_distributor")!!
                    namaUsaha.text = item.nama_usaha
                    alamat.text = item.alamat
                    jarak.text = "${"%.2f".format(distance)} km"
                    if (item.jenis_kelamin == "Perempuan") {
                        namaPemilik.text = "Ibu ${item.username}"
                    } else {
                        namaPemilik.text = "Bapak ${item.username}"
                    }

                    Glide.with(this@BuatTransaksiActivity)
                        .load(img)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePenjual)
                }
            }

            override fun onFailure(call: Call<DataPenjual>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}